package com.example.kotlinmodule4

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.*
import kotlin.random.Random

class RandomNumberService : Service() {

    private val binder = RandomBinder()
    private val scope = CoroutineScope(Dispatchers.Default)

    var currentNumber = 0
        private set

    inner class RandomBinder : Binder() {
        fun getService(): RandomNumberService = this@RandomNumberService
    }

    override fun onBind(intent: Intent?): IBinder {
        startGenerating()
        return binder
    }

    private fun startGenerating() {

        scope.launch {

            while (true) {

                delay(1000)

                currentNumber = Random.nextInt(0, 101)

            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        scope.cancel()
        return super.onUnbind(intent)
    }
}