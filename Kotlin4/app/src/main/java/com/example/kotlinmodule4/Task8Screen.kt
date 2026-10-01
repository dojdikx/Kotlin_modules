package com.example.kotlinmodule4

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.collectLatest

private const val PHOTO_WORK = "photo-pipeline"

@Composable
fun Task8Screen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var status by remember { mutableStateOf("Ожидание") }
    var fraction by remember { mutableStateOf(0f) }
    var running by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    var runId by remember { mutableIntStateOf(0) }

    LaunchedEffect(runId) {
        if (runId == 0) return@LaunchedEffect
        workManager.getWorkInfosForUniqueWorkFlow(PHOTO_WORK).collectLatest { infos ->
            val current = infos.lastOrNull { it.state == WorkInfo.State.RUNNING }
                ?: infos.lastOrNull()
            if (current != null) {
                fraction = current.progress.getInt("progress", 0) / 100f
                current.progress.getString("status")?.let { status = it }
            }
            when {
                infos.any { it.state == WorkInfo.State.FAILED || it.state == WorkInfo.State.CANCELLED } -> {
                    status = "Ошибка обработки"
                    result = "Цепочка остановлена"
                    running = false
                    workManager.cancelUniqueWork(PHOTO_WORK)
                }
                infos.isNotEmpty() && infos.all { it.state == WorkInfo.State.SUCCEEDED } -> {
                    result = infos.last().outputData.getString("result").orEmpty()
                    status = "Готово! Фото загружено"
                    fraction = 1f
                    running = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(status, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        if (running) {
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(result)
        Spacer(Modifier.height(20.dp))
        Button(
            enabled = !running,
            onClick = {
                val compress = OneTimeWorkRequestBuilder<CompressWorker>().build()
                val watermark = OneTimeWorkRequestBuilder<WatermarkWorker>().build()
                val upload = OneTimeWorkRequestBuilder<UploadWorker>().build()
                workManager.beginUniqueWork(PHOTO_WORK, ExistingWorkPolicy.REPLACE, compress)
                    .then(watermark)
                    .then(upload)
                    .enqueue()
                status = "Сжимаем фото…"
                result = ""
                fraction = 0f
                running = true
                runId += 1
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Начать обработку и загрузку")
        }
    }
}
