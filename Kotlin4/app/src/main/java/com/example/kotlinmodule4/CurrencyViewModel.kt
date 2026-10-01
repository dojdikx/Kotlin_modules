package com.example.kotlinmodule4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CurrencyViewModel : ViewModel() {

    private val _rate = MutableStateFlow(93.2)
    val rate: StateFlow<Double> = _rate

    private var lastRate = 93.2

    init {
        startAutoUpdates()
    }

    private fun startAutoUpdates() {

        viewModelScope.launch {

            while (true) {

                delay(5000)

                generateNewRate()
            }
        }
    }

    fun refreshNow() {

        generateNewRate()
    }

    private fun generateNewRate() {

        val change = Random.nextDouble(-2.0, 2.0)

        val newRate = (lastRate + change).coerceIn(85.0, 100.0)

        lastRate = newRate

        _rate.value = newRate
    }
}