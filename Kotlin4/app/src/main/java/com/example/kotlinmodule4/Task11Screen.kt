package com.example.kotlinmodule4

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun Task11Screen() {

    val context = LocalContext.current

    val prefs =
        context.getSharedPreferences("pill", android.content.Context.MODE_PRIVATE)

    var enabled by remember {
        mutableStateOf(prefs.getBoolean("enabled", false))
    }

    val notificationPermission =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Напоминание о таблетке",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (enabled) Color(0xFF3DDC84) else Color.Gray)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                if (enabled) "Включено" else "Выключено",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (enabled) {
            Text("Следующее напоминание: ${AlarmHelper.nextReminderDay()} в 20:00")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                if (!enabled) {

                    if (Build.VERSION.SDK_INT >= 33) {
                        notificationPermission.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }

                    AlarmHelper.scheduleAlarm(context)

                    prefs.edit().putBoolean("enabled", true).apply()

                    enabled = true

                } else {

                    AlarmHelper.cancelAlarm(context)

                    prefs.edit().putBoolean("enabled", false).apply()

                    enabled = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (enabled)
                    "Выключить напоминание"
                else
                    "Включить напоминание"
            )
        }
    }
}