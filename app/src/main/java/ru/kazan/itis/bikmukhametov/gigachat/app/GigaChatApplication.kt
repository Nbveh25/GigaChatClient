package ru.kazan.itis.bikmukhametov.gigachat.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GigaChatApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
