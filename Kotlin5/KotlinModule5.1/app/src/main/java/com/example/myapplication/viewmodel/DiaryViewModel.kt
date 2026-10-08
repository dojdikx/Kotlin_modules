package com.example.myapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.DiaryNote
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel : ViewModel() {

    val notes = mutableStateListOf<DiaryNote>()

    fun updateNote(
        context: Context,
        note: DiaryNote,
        newTitle: String,
        newText: String
    ) {

        val file = File(context.filesDir, note.fileName)

        file.writeText(
            "$newTitle\n$newText",
            Charsets.UTF_8
        )

        val index = notes.indexOf(note)

        if (index != -1) {

            notes[index] = note.copy(
                title = newTitle,
                text = newText,
                date = formatDate(System.currentTimeMillis())
            )
        }
    }

    fun loadNotes(context: Context) {

        notes.clear()

        val files = context.filesDir.listFiles()

        files?.forEach { file ->

            val text = file.readText(Charsets.UTF_8)

            val lines = text.split("\n")

            val title = lines.firstOrNull() ?: ""

            val content = lines.drop(1).joinToString("\n")

            val note = DiaryNote(
                fileName = file.name,
                title = title,
                text = content,
                date = formatDate(file.lastModified())
            )

            notes.add(note)
        }

        notes.reverse()
    }

    fun saveNote(
        context: Context,
        title: String,
        text: String
    ) {

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        val safeTitle = title
            .replace(" ", "_")
            .replace("/", "")
            .replace("\\", "")

        val fileName = if (safeTitle.isNotEmpty()) {
            "timestamp_${timeStamp}_$safeTitle.txt"
        } else {
            "timestamp_${timeStamp}.txt"
        }

        val file = File(context.filesDir, fileName)

        file.writeText(
            "$title\n$text",
            Charsets.UTF_8
        )

        val note = DiaryNote(
            fileName = fileName,
            title = title,
            text = text,
            date = formatDate(System.currentTimeMillis())
        )

        notes.add(0, note)
    }

    fun deleteNote(
        context: Context,
        note: DiaryNote
    ) {

        val file = File(context.filesDir, note.fileName)

        if (file.exists()) {
            file.delete()
        }

        notes.remove(note)
    }

    private fun formatDate(time: Long): String {

        return SimpleDateFormat(
            "dd.MM.yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(time))
    }
}