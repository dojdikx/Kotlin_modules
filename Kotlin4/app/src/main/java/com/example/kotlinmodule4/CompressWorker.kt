package com.example.kotlinmodule4

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class CompressWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            for (step in 10..100 step 10) {
                delay(200)
                setProgress(workDataOf("progress" to step, "status" to "Сжимаем фото…"))
            }
            Result.success(workDataOf("file" to "campus_evening.jpg"))
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
