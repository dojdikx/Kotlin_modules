package com.example.kotlinmodule4

import android.content.*
import android.os.IBinder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

@Composable
fun Task7Screen() {

    val context = LocalContext.current

    var number by remember { mutableStateOf(0) }
    var connected by remember { mutableStateOf(false) }

    var service: RandomNumberService? by remember { mutableStateOf(null) }

    val scope = rememberCoroutineScope()

    val connection = remember {

        object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {

                val randomBinder = binder as RandomNumberService.RandomBinder
                service = randomBinder.getService()

                connected = true

                scope.launch {

                    while (connected) {

                        delay(1000)

                        number = service?.currentNumber ?: 0
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                connected = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Случайное число: $number",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val intent = Intent(context, RandomNumberService::class.java)

                context.bindService(
                    intent,
                    connection,
                    Context.BIND_AUTO_CREATE
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подключиться")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                context.unbindService(connection)

                connected = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отключиться")
        }
    }
}