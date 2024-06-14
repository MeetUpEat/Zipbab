package com.bestapp.rice

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp



@HiltAndroidApp
class App : Application() {

    @AddTrace(name = "onCreate")
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY);
    }
}