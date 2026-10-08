package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.model.DiaryNote
import com.example.myapplication.viewmodel.DiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = viewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadNotes(context)
    }

    val navController = rememberNavController()

    var selectedNote by remember {
        mutableStateOf<DiaryNote?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {

        composable("list") {

            DiaryListScreen(
                notes = viewModel.notes,

                onAddClick = {
                    navController.navigate("add")
                },

                onNoteClick = { note ->

                    selectedNote = note

                    navController.navigate("edit")
                },

                onDelete = {
                    viewModel.deleteNote(context, it)
                }
            )
        }

        composable("add") {

            AddNoteScreen(

                onSave = { title, text ->

                    viewModel.saveNote(
                        context,
                        title,
                        text
                    )

                    navController.popBackStack()
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("edit") {

            selectedNote?.let { note ->

                EditNoteScreen(

                    note = note,

                    onSave = { title, text ->

                        viewModel.updateNote(
                            context,
                            note,
                            title,
                            text
                        )

                        navController.popBackStack()
                    },

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(
    notes: List<DiaryNote>,
    onAddClick: () -> Unit,
    onNoteClick: (DiaryNote) -> Unit,
    onDelete: (DiaryNote) -> Unit
) {

    var deleteNote by remember {
        mutableStateOf<DiaryNote?>(null)
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {
                    Text("Мой дневник")
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onAddClick
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

    ) { padding ->

        if (notes.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "У вас пока нет записей",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = "Нажмите +, чтобы создать первую",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentPadding = PaddingValues(8.dp)
            ) {

                items(notes) { note ->

                    DiaryItem(
                        note = note,

                        onClick = {
                            onNoteClick(note)
                        },

                        onLongClick = {
                            deleteNote = note
                        }
                    )
                }
            }
        }

        deleteNote?.let { note ->

            AlertDialog(

                onDismissRequest = {
                    deleteNote = null
                },

                title = {
                    Text("Удаление")
                },

                text = {
                    Text("Удалить запись?")
                },

                confirmButton = {

                    TextButton(

                        onClick = {

                            onDelete(note)

                            deleteNote = null
                        }

                    ) {
                        Text("Удалить")
                    }
                },

                dismissButton = {

                    TextButton(

                        onClick = {
                            deleteNote = null
                        }

                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}