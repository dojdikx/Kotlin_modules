package com.example.kotlinmodule4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlin.random.Random

class WeatherWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val city = inputData.getString("city") ?: return Result.failure()
        show("Загружаем погоду для 3 городов…")
        delay(2000)
        val temp = Random.nextInt(-5, 25)
        val done = finishedCities(city)
        val pending = CITIES.filter { it !in done }
        val text = if (pending.isEmpty()) {
            "Все данные получены, формируем отчёт…"
        } else {
            "Готово: ${done.joinToString(" и ")}, ${pending.joinToString(" и ")} в процессе…"
        }
        show(text)
        return Result.success(workDataOf(city to temp))
    }

    private fun finishedCities(city: String): List<String> {
        val prefs = applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val done = prefs.getStringSet(KEY, emptySet()).orEmpty().toMutableSet()
        done += city
        prefs.edit().putStringSet(KEY, done).apply()
        return CITIES.filter { it in done }
    }

    private suspend fun show(text: String) {
        setForeground(ForegroundInfo(NOTIFICATION_ID, buildNotification(applicationContext, text), foregroundType()))
    }

    companion object {
        const val PREFS = "weather_progress"
        const val KEY = "done"
        const val CHANNEL = "weather_report"
        const val NOTIFICATION_ID = 77
        val CITIES = listOf("Москва", "Лондон", "Нью-Йорк")

        fun clear(context: Context) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        }

        fun buildNotification(context: Context, text: String) =
            NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Прогноз погоды")
                .setContentText(text)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build()

        fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(CHANNEL, "Прогноз погоды", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        private fun foregroundType(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        }
    }
}
