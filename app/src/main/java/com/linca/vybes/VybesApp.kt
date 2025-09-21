package com.linca.vybes

import android.app.Application
import com.linca.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VybesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }
}