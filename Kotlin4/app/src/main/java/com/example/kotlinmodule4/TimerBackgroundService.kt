package com.example.kotlinmodule4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class TimerBackgroundService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val seconds = intent?.getIntExtra("seconds", 0) ?: 0

        scope.launch {

            delay(seconds * 1000L)

            showNotification()

            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun showNotification() {

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "timer_done",
                "Timer Done",
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "timer_done")
            .setContentTitle("Таймер завершён!")
            .setContentText("Время вышло")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(2, notification)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}