package com.bestapp.rice.ui.foodcategory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.Filter
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InputFoodCategoryViewModel(
    private val meetingRepository: MeetingRepository
) : ViewModel() {

//    private val _meetingList = MutableSharedFlow<List<MeetingUiState>>(replay = 0)
//    val meetingList: SharedFlow<List<MeetingUiState>>
//        get() = _meetingList

    private val _meetingList = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val meetingList: StateFlow<List<MeetingUiState>>
        get() = _meetingList

    private val _foodCategory = MutableStateFlow<List<FilterUiState.FoodUiState>>(emptyList())
    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>> = _foodCategory

    private val _goMeetingNavi = MutableSharedFlow<GoMeetingNavi>(replay = 0)
    val goMeetingNavi: SharedFlow<GoMeetingNavi>
        get() = _goMeetingNavi

    fun getFoodMeeting(mainMenu: String) {
        Log.e("cyc", "getFoodMeeting")

        viewModelScope.launch {
            runCatching {
                meetingRepository.getFoodMeeting(mainMenu)
            }.onSuccess {

                val meetingUiStateList = it.map { meeting ->
                    MeetingUiState.createFrom(meeting)
                }
                Log.e("cyc", "뷰모델--getFoodMeeting-meetingUiStateList-->${meetingUiStateList}")
                _meetingList.emit(meetingUiStateList)
            }
        }
    }
}