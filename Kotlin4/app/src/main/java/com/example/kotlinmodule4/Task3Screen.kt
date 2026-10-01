package com.example.kotlinmodule4

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*

data class Repo(
    val id: Int,
    val full_name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?
)

@Composable
fun Task3Screen() {

    val context = LocalContext.current

    var repos by remember { mutableStateOf<List<Repo>>(emptyList()) }
    var result by remember { mutableStateOf<List<Repo>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // загрузка JSON при запуске
    LaunchedEffect(Unit) {
        repos = loadRepos(context)
    }

    Column(Modifier.padding(16.dp)) {

        TextField(
            value = query,
            onValueChange = { text ->

                query = text

                searchJob?.cancel()

                searchJob = scope.launch {

                    delay(500) // debounce

                    loading = true

                    result = repos.filter {
                        it.full_name.contains(query, ignoreCase = true)
                    }

                    loading = false
                }
            },
            label = { Text("Поиск репозиториев") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        LazyColumn {

            items(result) { repo ->

                Card(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(repo.full_name, style = MaterialTheme.typography.titleMedium)
                        repo.description?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            "${repo.language ?: "—"} · ${repo.stargazers_count} звёзд",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

suspend fun loadRepos(context: Context): List<Repo> =
    withContext(Dispatchers.IO) {

        val json = context.assets
            .open("github_repos.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Repo>>() {}.type

        Gson().fromJson(json, type)
    }