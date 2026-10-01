package com.example.kotlinmodule4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val prefs =
                context.getSharedPreferences("pill", Context.MODE_PRIVATE)

            val enabled = prefs.getBoolean("enabled", false)

            if (enabled) {

                AlarmHelper.scheduleAlarm(context)
            }
        }
    }
}