package com.example.kotlinmodule4

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun Task5Screen() {

    val context = LocalContext.current

    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }

    // простой таймер для отображения в UI
    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Счётчик времени",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Прошло $seconds секунд",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {

                val intent = Intent(context, TimerService::class.java)
                context.startService(intent)

                running = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Старт")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                val intent = Intent(context, TimerService::class.java)
                context.stopService(intent)

                running = false
                seconds = 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Стоп")
        }
    }
}