package com.example.kotlinmodule4

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun Task12Screen(
    viewModel: FactsViewModel = viewModel()
) {

    var fact by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Факты о животных",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                scope.launch {

                    loading = true
                    fact = ""

                    viewModel.getRandomFact().collect {

                        fact = it
                        loading = false
                    }
                }

            }
        ) {
            Text("Новый факт!")
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (loading) {

            CircularProgressIndicator()

        }

        AnimatedVisibility(
            visible = fact.isNotEmpty(),
            enter = fadeIn()
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Text(
                    text = fact,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }
    }
}