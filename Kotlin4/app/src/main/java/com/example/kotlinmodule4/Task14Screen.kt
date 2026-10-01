package com.example.kotlinmodule4

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Task14Screen(
    viewModel: CompassViewModel = viewModel()
) {

    val context = LocalContext.current

    val azimuth by viewModel.azimuth.collectAsState()

    val animatedRotation by animateFloatAsState(
        targetValue = -azimuth,
        label = "compassRotation"
    )

    val sensorManager = remember {
        CompassSensorManager(context) {
            viewModel.updateAzimuth(it)
        }
    }

    DisposableEffect(Unit) {

        if (sensorManager.hasSensors()) {
            sensorManager.start()
        }

        onDispose {
            sensorManager.stop()
        }
    }

    if (!sensorManager.hasSensors()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Устройство не поддерживает датчик ориентации",
                color = Color.Red
            )
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Компас",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                val radius = size.minDimension / 2

                drawCircle(
                    color = Color(0xFFF2F2F2),
                    radius = radius
                )

                rotate(animatedRotation) {

                    drawLine(
                        color = Color.Red,
                        start = Offset(radius, radius),
                        end = Offset(radius, radius * 0.2f),
                        strokeWidth = 12f
                    )

                    drawLine(
                        color = Color.Black,
                        start = Offset(radius, radius),
                        end = Offset(radius, radius * 1.8f),
                        strokeWidth = 12f
                    )
                }
            }

            Text(
                text = "N",
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Азимут: ${azimuth.toInt()}°",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}