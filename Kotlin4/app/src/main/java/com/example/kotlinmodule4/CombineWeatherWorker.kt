package com.example.kotlinmodule4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class CombineWeatherWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val temperatures = inputData.keyValueMap.values.mapNotNull { it as? Int }
        val avg = if (temperatures.isEmpty()) 0 else temperatures.sum() / temperatures.size
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
            as android.app.NotificationManager
        manager.notify(
            WeatherWorker.NOTIFICATION_ID,
            WeatherWorker.buildNotification(
                applicationContext,
                "Отчёт готов! Средняя температура $avg°C"
            )
        )
        return Result.success(workDataOf("avg_temp" to avg))
    }
}
