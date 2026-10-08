package com.example.module6.task1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.module6.task1.di.BaseApplication
import com.example.module6.task1.presentation.BigPhotoScreen
import com.example.module6.task1.presentation.ListScreen
import com.example.module6.task1.ui.theme.Module6Task1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as BaseApplication).container
        setContent {
            Module6Task1Theme {
                Surface {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = "list") {
                        composable("list") {
                            ListScreen(
                                getPhotos = container.getPhotoUseCase,
                                onPhotoClick = { id -> nav.navigate("detail/$id") }
                            )
                        }
                        composable("detail/{id}") { entry ->
                            val id = entry.arguments?.getString("id") ?: return@composable
                            BigPhotoScreen(
                                photoId = id,
                                repo = container.photoRepository,
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

