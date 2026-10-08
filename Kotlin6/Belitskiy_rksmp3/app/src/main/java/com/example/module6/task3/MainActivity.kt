package com.example.module6.task3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module6.task3.di.BaseApplication
import com.example.module6.task3.presentation.LoginScreen
import com.example.module6.task3.presentation.UserDetailScreen
import com.example.module6.task3.presentation.UsersListScreen
import com.example.module6.task3.ui.theme.Module6Task3Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c = (application as BaseApplication).container
        val startToken = runBlocking { c.tokenStore.tokenFlow.first() }
        val start = if (startToken.isNullOrBlank()) "login" else "users"
        setContent {
            Module6Task3Theme {
                Surface {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = start) {
                        composable("login") {
                            LoginScreen(
                                loginUseCase = c.loginUseCase,
                                onLoggedIn = {
                                    nav.navigate("users") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("users") {
                            UsersListScreen(
                                getUsers = c.getUsersUseCase,
                                onUserClick = { id -> nav.navigate("user/$id") }
                            )
                        }
                        composable("user/{id}") { entry ->
                            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
                            UserDetailScreen(
                                id = id,
                                getById = c.getUserByIdUseCase,
                                logoutUseCase = c.logoutUseCase,
                                onBack = { nav.popBackStack() },
                                onLoggedOut = {
                                    nav.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

