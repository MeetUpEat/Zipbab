package com.bestapp.rice.ui.foodcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.FakeAppSettingRepositoryImpl
import com.bestapp.rice.data.repository.FakeCategoryRepositoryImpl
import com.bestapp.rice.data.repository.FakeMeetingRepositoryImpl
import com.bestapp.rice.data.repository.MeetingRepository

class FoodCategoryFactory : ViewModelProvider.Factory {

    private val meetingRepository: MeetingRepository = FakeMeetingRepositoryImpl()
    private val categoryRepository: CategoryRepository = FakeCategoryRepositoryImpl()
    private val appSettingRepository: AppSettingRepository = FakeAppSettingRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            FoodCategoryViewModel::class.java -> FoodCategoryViewModel(meetingRepository, categoryRepository, appSettingRepository, extras.createSavedStateHandle())
            else -> throw IllegalArgumentException()
        } as T
    }
}


