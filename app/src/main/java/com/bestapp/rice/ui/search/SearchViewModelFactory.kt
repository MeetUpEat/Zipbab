package com.bestapp.rice.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.FakeAppSettingRepositoryImpl
import com.bestapp.rice.data.repository.FakeMeetingRepositoryImp
import com.bestapp.rice.data.repository.MeetingRepository


class SearchViewModelFactory : ViewModelProvider.Factory {

    private val meetingRepository: MeetingRepository = FakeMeetingRepositoryImp()
    private val appSettingRepository: AppSettingRepository = FakeAppSettingRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SearchViewModel::class.java -> SearchViewModel(meetingRepository, appSettingRepository)
            else -> throw IllegalArgumentException()
        } as T
    }

}