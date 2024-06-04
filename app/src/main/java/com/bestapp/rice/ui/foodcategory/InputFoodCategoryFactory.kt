package com.bestapp.rice.ui.foodcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.bestapp.rice.data.repository.FakeMeetingRepositoryImp
import com.bestapp.rice.data.repository.MeetingRepository

class InputFoodCategoryFactory : ViewModelProvider.Factory {

    private val meetingRepository: MeetingRepository = FakeMeetingRepositoryImp()

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            InputFoodCategoryViewModel::class.java -> InputFoodCategoryViewModel(meetingRepository)
            else -> throw IllegalArgumentException()
        } as T
    }
}
