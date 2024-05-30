package com.bestapp.rice

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace

class App : Application() {

    @AddTrace(name = "onCreate")
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }
}