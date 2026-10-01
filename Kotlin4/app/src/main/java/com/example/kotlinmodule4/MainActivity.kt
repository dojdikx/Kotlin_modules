package com.example.kotlinmodule4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinmodule4.ui.theme.KotlinModule4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinModule4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

private data class PracticeTask(val id: Int, val title: String)

private val practiceTasks = listOf(
    PracticeTask(3, "Поиск репозиториев"),
    PracticeTask(4, "Социальная лента"),
    PracticeTask(5, "Счётчик времени"),
    PracticeTask(6, "Одноразовый таймер"),
    PracticeTask(7, "Случайное число"),
    PracticeTask(8, "Обработка фото"),
    PracticeTask(9, "Прогноз погоды"),
    PracticeTask(10, "Моё местоположение"),
    PracticeTask(11, "Напоминание о таблетке"),
    PracticeTask(12, "Факты о животных"),
    PracticeTask(13, "Курс валют"),
    PracticeTask(14, "Компас")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot() {
    var openedTask by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (openedTask == null) "Модуль 4" else "Задание $openedTask")
                }
            )
        }
    ) { padding ->
        if (openedTask == null) {
            TaskMenu(padding) { openedTask = it }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = { openedTask = null },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("К списку заданий")
                }
                Box(Modifier.weight(1f).fillMaxWidth()) {
                when (openedTask) {
                    3 -> Task3Screen()
                    4 -> Task4Screen()
                    5 -> Task5Screen()
                    6 -> Task6Screen()
                    7 -> Task7Screen()
                    8 -> Task8Screen()
                    9 -> Task9Screen()
                    10 -> Task10Screen()
                    11 -> Task11Screen()
                    12 -> Task12Screen()
                    13 -> Task13Screen()
                    14 -> Task14Screen()
                }
                }
            }
        }
    }
}

@Composable
private fun TaskMenu(padding: PaddingValues, onOpen: (Int) -> Unit) {
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(practiceTasks) { task ->
            Button(
                onClick = { onOpen(task.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${task.id}. ${task.title}")
            }
        }
    }
}
