package com.bestapp.rice.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.network.FirebaseClient
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.AppSettingRepositoryImpl
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.data.repository.UserRepositoryImpl
import com.bestapp.rice.dataStore

class ProfileViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseClient.userStoreService)
    private val appSettingRepository: AppSettingRepository = AppSettingRepositoryImpl(context.dataStore, FirebaseClient.privacyStoreService)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ProfileViewModel::class.java -> ProfileViewModel(userRepository, appSettingRepository)
            else -> throw IllegalArgumentException()
        } as T
    }
}