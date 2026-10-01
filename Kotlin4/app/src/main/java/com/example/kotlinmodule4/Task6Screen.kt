package com.example.kotlinmodule4

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun Task6Screen() {

    val context = LocalContext.current
    var secondsText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        OutlinedTextField(
            value = secondsText,
            onValueChange = { secondsText = it },
            label = { Text("Введите секунды") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                val seconds = secondsText.toIntOrNull() ?: 0

                val intent = Intent(context, TimerBackgroundService::class.java)
                intent.putExtra("seconds", seconds)

                context.startService(intent)

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Запустить таймер")

        }
    }
}