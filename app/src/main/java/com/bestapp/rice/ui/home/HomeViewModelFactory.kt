package com.bestapp.rice.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.network.FirebaseClient
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.AppSettingRepositoryImpl
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.CategoryRepositoryImpl
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.MeetingRepositoryImpl
import com.bestapp.rice.dataStore

class HomeViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val appSettingRepository: AppSettingRepository = AppSettingRepositoryImpl(context.dataStore, FirebaseClient.privacyStoreService)
    private val categoryRepository: CategoryRepository = CategoryRepositoryImpl(FirebaseClient.categoryStoreService)
    private val meetingRepository: MeetingRepository = MeetingRepositoryImpl(FirebaseClient.store, FirebaseClient.meetingStoreService)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){
            HomeViewModel::class.java -> HomeViewModel(appSettingRepository,categoryRepository,meetingRepository)
            else -> throw IllegalArgumentException()
        } as T
    }

}