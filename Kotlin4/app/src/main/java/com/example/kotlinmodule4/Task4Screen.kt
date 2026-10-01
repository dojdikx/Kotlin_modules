package com.example.kotlinmodule4

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val avatarUrl: String
)

data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val body: String
)

sealed class PostState {
    data object Loading : PostState()
    data class Ready(val avatar: String, val comments: List<Comment>) : PostState()
    data object Error : PostState()
}

@Composable
fun Task4Screen() {
    val context = LocalContext.current
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var postStates by remember { mutableStateOf<Map<Int, PostState>>(emptyMap()) }
    val scope = rememberCoroutineScope()
    var feedJob by remember { mutableStateOf<Job?>(null) }

    fun loadFeed() {
        feedJob?.cancel()
        postStates = emptyMap()
        feedJob = scope.launch {
            try {
                posts = loadPosts(context)
                val comments = loadAllComments(context)
                supervisorScope {
                    posts.map { post ->
                        async {
                            postStates = postStates + (post.id to PostState.Loading)
                            try {
                                val avatar = async { loadAvatar(post.avatarUrl) }
                                val postComments = async { loadComments(comments, post.id) }
                                postStates = postStates + (
                                    post.id to PostState.Ready(
                                        avatar = avatar.await(),
                                        comments = postComments.await()
                                    )
                                )
                            } catch (error: CancellationException) {
                                throw error
                            } catch (_: Exception) {
                                postStates = postStates + (post.id to PostState.Error)
                            }
                        }
                    }.awaitAll()
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                posts.forEach { post ->
                    postStates = postStates + (post.id to PostState.Error)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFeed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Button(
            onClick = { loadFeed() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Обновить")
        }
        LazyColumn {
            items(posts) { post ->
                PostCard(post, postStates[post.id] ?: PostState.Loading)
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, state: PostState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(post.body, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            when (state) {
                PostState.Loading -> CircularProgressIndicator()
                is PostState.Ready -> {
                    Row {
                        Spacer(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(state.avatar.hashCode() or 0xFF000000.toInt()))
                        )
                        Column(Modifier.padding(start = 12.dp)) {
                            if (state.comments.isEmpty()) {
                                Text("Комментариев пока нет")
                            } else {
                                state.comments.forEach { comment ->
                                    Text("${comment.name}: ${comment.body}")
                                }
                            }
                        }
                    }
                }
                PostState.Error -> Text("Не удалось загрузить аватар или комментарии")
            }
        }
    }
}

suspend fun loadAvatar(url: String): String = withContext(Dispatchers.IO) {
    delay(700)
    url
}

suspend fun loadComments(comments: List<Comment>, postId: Int): List<Comment> =
    withContext(Dispatchers.IO) {
        delay(900)
        comments.filter { it.postId == postId }
    }

suspend fun loadPosts(context: Context): List<Post> = withContext(Dispatchers.IO) {
    val json = context.assets.open("social_posts.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Post>>() {}.type
    Gson().fromJson(json, type)
}

suspend fun loadAllComments(context: Context): List<Comment> = withContext(Dispatchers.IO) {
    val json = context.assets.open("comments.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Comment>>() {}.type
    Gson().fromJson(json, type)
}
