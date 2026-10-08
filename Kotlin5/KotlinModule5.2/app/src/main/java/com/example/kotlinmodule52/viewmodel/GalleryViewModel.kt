package com.example.kotlinmodule52.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kotlinmodule52.model.PhotoItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryViewModel : ViewModel() {

    val photos = mutableStateListOf<PhotoItem>()

    fun loadPhotos(context: Context) {

        photos.clear()

        val directory = context.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )

        val files = directory?.listFiles()

        files
            ?.filter { it.isFile && it.length() > 0L && it.extension.equals("jpg", true) }
            ?.sortedByDescending { it.lastModified() }
            ?.forEach { file ->
                photos.add(PhotoItem(path = file.absolutePath))
            }
    }

    fun deletePhoto(photo: PhotoItem) {
        File(photo.path).delete()
        photos.remove(photo)
    }

    fun addPhoto(path: String) {

        photos.add(
            0,
            PhotoItem(path)
        )
    }

    fun createImageFile(context: Context): File {

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        val storageDir = context.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )

        return File.createTempFile(
            "IMG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun exportToGallery(context: Context, path: String): Boolean {
        val source = File(path)
        if (!source.exists()) return false
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, source.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/KotlinGallery"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        return try {
            resolver.openOutputStream(uri)?.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            } ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            true
        } catch (_: Exception) {
            resolver.delete(uri, null, null)
            false
        }
    }
}