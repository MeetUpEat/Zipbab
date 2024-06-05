package com.bestapp.rice.ui.foodcategory

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

class FoodCategoryViewModel(
    private val meetingRepository: MeetingRepository,
    private val categoryRepository: CategoryRepository,
    private val appSettingRepository: AppSettingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meetingList = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val meetingList: StateFlow<List<MeetingUiState>>
        get() = _meetingList

    private val _foodCategory = MutableStateFlow<List<FilterUiState.FoodUiState>>(emptyList())
    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>> = _foodCategory

    private val _goMeetingNavi = MutableSharedFlow<MoveMeetingNavi>(replay = 0)
    val goMeetingNavi: SharedFlow<MoveMeetingNavi>
        get() = _goMeetingNavi

    var selectIndex = DEFAULT_INDEX
    var selectMenu = ""
    var meetingDocumentID = ""

    init {
        savedStateHandle.get<FilterUiState.FoodUiState>("foodCategory")?.let {
            selectMenu = it.name
            getFoodMeeting(selectMenu)
        }

        viewModelScope.launch {
            runCatching {
                categoryRepository.getFoodCategory()
            }.onSuccess {
                val foodUiStateList = it.mapIndexed { index, filter ->
                    savedStateHandle.get<FilterUiState.FoodUiState>("foodCategory")
                        ?.let { foodUiState ->
                            if (foodUiState.name == FilterUiState.FoodUiState.createFrom(filter as Filter.Food).name) {
                                selectIndex = index
                            }
                        }
                    FilterUiState.FoodUiState.createFrom(filter as Filter.Food)

                }
                _foodCategory.value = foodUiStateList
            }
        }

    }

    fun getFoodMeeting(mainMenu: String) {
        viewModelScope.launch {
            runCatching {
                meetingRepository.getFoodMeeting(mainMenu)
            }.onSuccess {

                val meetingUiStateList = it.map { meeting ->
                    MeetingUiState.createFrom(meeting)
                }
                _meetingList.emit(meetingUiStateList)
            }
        }
    }

    fun goMeeting(meetingUiState: MeetingUiState) {
        this.meetingDocumentID = meetingUiState.meetingDocumentID
        viewModelScope.launch {
            runCatching {
                appSettingRepository.getUserInfo()
            }.onSuccess {
                if (UserUiState.createFrom(it).userDocumentID == meetingUiState.host) {
                    _goMeetingNavi.emit(MoveMeetingNavi.GO_MEETING_MANAGEMENT)
                } else {
                    _goMeetingNavi.emit(MoveMeetingNavi.GO_MEETING_INFO)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_INDEX = 0
    }
}