package com.bestapp.rice.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.FakeAppSettingRepositoryImpl
import com.bestapp.rice.data.repository.FakeCategoryRepositoryImpl
import com.bestapp.rice.data.repository.FakeMeetingRepositoryImp
import com.bestapp.rice.data.repository.MeetingRepository

class HomeViewModelFactory: ViewModelProvider.Factory {

    private val appSettingRepository: AppSettingRepository = FakeAppSettingRepositoryImpl()
    private val categoryRepository: CategoryRepository = FakeCategoryRepositoryImpl()
    private val meetingRepository: MeetingRepository = FakeMeetingRepositoryImp()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){
            HomeViewModel::class.java -> HomeViewModel(appSettingRepository,categoryRepository,meetingRepository)
            else -> throw IllegalArgumentException()
        } as T
    }

}