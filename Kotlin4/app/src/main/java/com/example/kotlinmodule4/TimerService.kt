package com.example.kotlinmodule4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class TimerService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var seconds = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = buildNotification("Прошло 0 секунд")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1, notification)
        }

        startTimer()
    }

    private fun startTimer() {

        scope.launch {

            while (true) {

                delay(1000)

                seconds++

                val notification = buildNotification("Прошло $seconds секунд")

                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(1, notification)
            }
        }
    }

    private fun buildNotification(text: String): Notification {

        return NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle("Счётчик времени")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "timer_channel",
                "Счётчик времени",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}