package com.bestapp.zipbab

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @AddTrace(name = "onCreate")
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}