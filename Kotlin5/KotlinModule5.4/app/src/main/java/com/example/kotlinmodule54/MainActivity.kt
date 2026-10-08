package com.example.kotlinmodule54

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.kotlinmodule54.ui.screens.TaskScreen
import com.example.kotlinmodule54.ui.theme.KotlinModule54Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            KotlinModule54Theme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    TaskScreen()
                }
            }
        }
    }
}