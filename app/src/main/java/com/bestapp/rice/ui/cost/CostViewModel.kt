package com.bestapp.rice.ui.cost

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
class CostViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val categoryRepository: CategoryRepository,
    private val appSettingRepository: AppSettingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meetingList = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val meetingList: StateFlow<List<MeetingUiState>>
        get() = _meetingList

    private val _costCategory = MutableStateFlow<List<FilterUiState.CostUiState>>(emptyList())
    val costCategory: StateFlow<List<FilterUiState.CostUiState>> = _costCategory

    private val _goMeetingNavi = MutableSharedFlow<Pair<MoveMeetingNavi, String>>(replay = 0)
    val goMeetingNavi: SharedFlow<Pair<MoveMeetingNavi, String>>
        get() = _goMeetingNavi

    var selectIndex = DEFAULT_INDEX
    var selectCost = DEFAULT_COST_TYPE
    var meetingDocumentID = ""

    init {
        savedStateHandle.get<FilterArg.CostArg>("costCategory")?.let {
            selectCost = it.type
            getCostMeeting(selectCost)
        }

        viewModelScope.launch {
            runCatching {
                categoryRepository.getCostCategory()
            }.onSuccess {
                val costUiStateList = it.mapIndexed { index, filter ->
                    savedStateHandle.get<FilterArg.CostArg>("costCategory")
                        ?.let { costUiState ->
                            if (costUiState.type == filter.toUiState().type) {
                                selectIndex = index
                            }
                        }
                    filter.toUiState()

                }
                _costCategory.value = costUiStateList
            }
        }

    }

    private fun getCostMeeting(costType: Int) {
        viewModelScope.launch {
            runCatching {
                meetingRepository.getCostMeeting(costType)
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
                    _goMeetingNavi.emit(
                        Pair(
                            MoveMeetingNavi.GO_MEETING_MANAGEMENT,
                            meetingUiState.meetingDocumentID
                        )
                    )
                } else {
                    _goMeetingNavi.emit(
                        Pair(
                            MoveMeetingNavi.GO_MEETING_INFO,
                            meetingUiState.meetingDocumentID
                        )
                    )
                }
            }
        }
    }

    fun selectTab(name: String, position: Int) {
        selectIndex = position
        _costCategory.value.forEach { costUiState ->
            if (costUiState.name == name) {
                getCostMeeting(costUiState.type)
            }
        }
    }

    companion object {
        private const val DEFAULT_INDEX = 0
        private const val DEFAULT_COST_TYPE = -1
    }

}