package com.example.kotlinmodule52.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmodule52.model.PhotoItem
import com.example.kotlinmodule52.viewmodel.GalleryViewModel
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentPhotoPath by remember { mutableStateOf("") }
    var openedPhoto by remember { mutableStateOf<PhotoItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPhotos(context)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = File(currentPhotoPath)
        if (success && file.length() > 0L) {
            viewModel.addPhoto(currentPhotoPath)
        } else {
            file.delete()
        }
    }

    fun openCamera() {
        val photoFile = viewModel.createImageFile(context)
        currentPhotoPath = photoFile.absolutePath
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Нужно разрешение на камеру")
            }
        }
    }

    fun takePhoto() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) openCamera() else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun export(photo: PhotoItem) {
        val saved = viewModel.exportToGallery(context, photo.path)
        scope.launch {
            snackbarHostState.showSnackbar(
                if (saved) "Фото добавлено в галерею" else "Не удалось экспортировать фото"
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Фотогалерея") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { takePhoto() }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Сделать фото")
            }
        }
    ) { padding ->
        if (viewModel.photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("У вас пока нет фото")
                    Button(
                        onClick = { takePhoto() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Сделать первое фото")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(viewModel.photos) { photo ->
                    PhotoTile(
                        photo = photo,
                        onOpen = { openedPhoto = photo },
                        onExport = { export(photo) },
                        onDelete = {
                            if (openedPhoto == photo) openedPhoto = null
                            viewModel.deletePhoto(photo)
                        }
                    )
                }
            }
        }
    }

    openedPhoto?.let { photo ->
        Dialog(onDismissRequest = { openedPhoto = null }) {
            Column(Modifier.padding(16.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(File(photo.path)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Button(
                    onClick = {
                        export(photo)
                        openedPhoto = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Экспорт в галерею")
                }
            }
        }
    }
}

@Composable
private fun PhotoTile(
    photo: PhotoItem,
    onOpen: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    Box(Modifier.aspectRatio(1f).fillMaxWidth()) {
        Image(
            painter = rememberAsyncImagePainter(File(photo.path)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = { menu = true },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Меню фото")
        }
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            DropdownMenuItem(
                text = { Text("Открыть") },
                onClick = {
                    menu = false
                    onOpen()
                }
            )
            DropdownMenuItem(
                text = { Text("Экспорт в галерею") },
                onClick = {
                    menu = false
                    onExport()
                }
            )
            DropdownMenuItem(
                text = { Text("Удалить") },
                onClick = {
                    menu = false
                    onDelete()
                }
            )
        }
    }
}
