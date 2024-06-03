package com.bestapp.rice

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.bestapp.rice.data.datastore.UserPreferences
import com.bestapp.rice.data.datastore.UserPreferencesSerializer
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace

/**
 * Hilt를 사용하면 DataStoreFactory를 이용해서 필요한 Repository에 주입해야 함
 * https://developer.android.com/codelabs/android-preferences-datastore#5
 * https://developer.android.com/reference/kotlin/androidx/datastore/core/DataStoreFactory
 */
val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = "UserPreferences.pb",
    serializer = UserPreferencesSerializer
)

class App : Application() {

    @AddTrace(name = "onCreate")
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }
}