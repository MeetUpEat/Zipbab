package com.bestapp.rice.ui.foodcategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.CategoryRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.model.args.FilterArg
import com.bestapp.rice.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodCategoryViewModel @Inject constructor(
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

    private val _goMeetingNavi = MutableSharedFlow<Pair<MoveMeetingNavi,String>>(replay = 0)
    val goMeetingNavi: SharedFlow<Pair<MoveMeetingNavi,String>>
        get() = _goMeetingNavi

    var selectIndex = DEFAULT_INDEX
    var selectMenu = ""
    var meetingDocumentID = ""

    init {
        savedStateHandle.get<FilterArg.FoodArg>(SAVEDSTATEHANDLE_KEY)?.let {
            selectMenu = it.name
            getFoodMeeting(selectMenu)
        }

        viewModelScope.launch {
            runCatching {
                categoryRepository.getFoodCategory()
            }.onSuccess {
                val foodUiStateList = it.mapIndexed { index, filter ->
                    savedStateHandle.get<FilterArg.FoodArg>(SAVEDSTATEHANDLE_KEY)
                        ?.let { foodUiState ->
                            if (foodUiState.name == filter.toUiState().name) {
                                selectIndex = index
                            }
                        }
                    filter.toUiState()

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
                    meeting.toUiState()
                }
                _meetingList.emit(meetingUiStateList)
            }
        }
    }

    fun goMeeting(meetingUiState: MeetingUiState) {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collect {
                if (it == meetingUiState.host) {
                    _goMeetingNavi.emit(Pair(MoveMeetingNavi.GO_MEETING_MANAGEMENT, meetingUiState.meetingDocumentID))
                } else {
                    _goMeetingNavi.emit(Pair(MoveMeetingNavi.GO_MEETING_INFO, meetingUiState.meetingDocumentID))
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_INDEX = 0
        private const val SAVEDSTATEHANDLE_KEY ="foodCategory"
    }
}