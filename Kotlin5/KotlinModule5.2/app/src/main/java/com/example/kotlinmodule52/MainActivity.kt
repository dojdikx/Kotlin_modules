package com.example.kotlinmodule52

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.kotlinmodule52.ui.screens.GalleryScreen
import com.example.kotlinmodule52.ui.theme.KotlinModule52Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            KotlinModule52Theme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    GalleryScreen()
                }
            }
        }
    }
}