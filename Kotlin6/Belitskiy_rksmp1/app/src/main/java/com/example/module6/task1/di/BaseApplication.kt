package com.example.module6.task1.di

import android.app.Application


class BaseApplication : Application() {
    lateinit var container: ApplicationContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = ApplicationContainer()
    }
}