package ru.kazan.itis.bikmukhametov.gigachat.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GigaChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
        //}
    }
}
