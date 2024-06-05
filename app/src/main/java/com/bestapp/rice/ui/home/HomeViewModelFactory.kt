package com.bestapp.rice.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.AppSettingRepositoryImpl
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.CategoryRepositoryImpl
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.MeetingRepositoryImpl

class HomeViewModelFactory: ViewModelProvider.Factory {

    private val appSettingRepository: AppSettingRepository = AppSettingRepositoryImpl()
    private val categoryRepository: CategoryRepository = CategoryRepositoryImpl()
    private val meetingRepository: MeetingRepository = MeetingRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){
            HomeViewModel::class.java -> HomeViewModel(appSettingRepository,categoryRepository,meetingRepository)
            else -> throw IllegalArgumentException()
        } as T
    }

}