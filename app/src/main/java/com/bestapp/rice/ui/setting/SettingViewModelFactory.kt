package com.bestapp.rice.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.FireBaseMessageReceiver
import com.bestapp.rice.data.network.FirebaseClient
import com.bestapp.rice.data.network.FirebaseClient
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.AppSettingRepositoryImpl
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.data.repository.UserRepositoryImpl
import com.bestapp.rice.dataStore

/**
 * Hilt 설정 전까지 임시 사용
 */
class SettingViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val appSettingRepository: AppSettingRepository = AppSettingRepositoryImpl(context.dataStore, FirebaseClient.privacyStoreService)
    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseClient.userStoreService)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SettingViewModel::class.java -> SettingViewModel(appSettingRepository, userRepository)
            else -> throw IllegalArgumentException()
        } as T
    }
}