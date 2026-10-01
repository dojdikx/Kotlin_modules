package com.example.kotlinmodule4

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Task13Screen(
    viewModel: CurrencyViewModel = viewModel()
) {

    val rate by viewModel.rate.collectAsState()

    var previousRate by remember { mutableStateOf(rate) }

    val directionUp = rate > previousRate

    LaunchedEffect(rate) {
        previousRate = rate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "USD → RUB",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = String.format("%.2f ₽", rate),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = if (directionUp)
                    Icons.Default.ArrowUpward
                else
                    Icons.Default.ArrowDownward,

                contentDescription = null,

                tint = if (directionUp) Color.Green else Color.Red
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {

                viewModel.refreshNow()

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Обновить сейчас")

        }
    }
}