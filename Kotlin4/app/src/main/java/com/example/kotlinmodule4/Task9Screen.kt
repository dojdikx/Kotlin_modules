package com.example.kotlinmodule4

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Task9Screen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var status by remember { mutableStateOf("Ожидание") }
    var result by remember { mutableStateOf("") }
    var combineId by remember { mutableStateOf<java.util.UUID?>(null) }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(combineId) {
        val id = combineId ?: return@LaunchedEffect
        workManager.getWorkInfoByIdFlow(id).collectLatest { info ->
            if (info?.state == WorkInfo.State.SUCCEEDED) {
                val avg = info.outputData.getInt("avg_temp", 0)
                status = "Отчёт готов!"
                result = "Средняя температура $avg°C"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Прогноз погоды", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        Text(status)
        Spacer(Modifier.height(10.dp))
        Text(result)
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = {
                WeatherWorker.ensureChannel(context)
                WeatherWorker.clear(context)
                status = "Загружаем погоду для 3 городов…"
                result = ""
                val cities = WeatherWorker.CITIES.map { city ->
                    OneTimeWorkRequestBuilder<WeatherWorker>()
                        .setInputData(workDataOf("city" to city))
                        .build()
                }
                val combine = OneTimeWorkRequestBuilder<CombineWeatherWorker>().build()
                workManager.beginWith(cities).then(combine).enqueue()
                combineId = combine.id
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Собрать прогноз")
        }
    }
}
