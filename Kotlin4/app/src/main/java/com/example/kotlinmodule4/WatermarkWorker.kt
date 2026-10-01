package com.example.kotlinmodule4

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class WatermarkWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val source = inputData.getString("file") ?: "campus_evening.jpg"
        return try {
            for (step in 10..100 step 10) {
                delay(200)
                setProgress(workDataOf("progress" to step, "status" to "Добавляем водяной знак…"))
            }
            Result.success(workDataOf("file" to "signed_$source"))
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
