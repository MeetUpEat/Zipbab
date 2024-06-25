package com.bestapp.zipbab.ui.cost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.CategoryRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.model.FilterUiState
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.args.FilterArgs
import com.bestapp.zipbab.model.toUiState
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

    private val _isLogin = MutableStateFlow<Boolean>(false)
    val isLogin: StateFlow<Boolean>
        get() = _isLogin

    private var selectCost = DEFAULT_COST_TYPE
    private var selectIndex = DEFAULT_INDEX

    init {
        savedStateHandle.get<FilterArgs.CostArgs>("costCategory")?.let {
            selectCost = it.type
            getCostMeeting(selectCost)
        }

        viewModelScope.launch {
            runCatching {
                categoryRepository.getCostCategory()
            }.onSuccess {
                val costUiStateList = it.mapIndexed { index, filter ->
                    savedStateHandle.get<FilterArgs.CostArgs>("costCategory")
                        ?.let { costUiState ->
                            if (costUiState.type == filter.toUiState().type) {
                                selectIndex = index
                            }
                        }
                    filter.toUiState()

                }
                _costCategory.value = costUiStateList

                appSettingRepository.userPreferencesFlow.collect { userDocumentId ->
                    _isLogin.emit(userDocumentId.isNotEmpty())
                }
            }
        }

    }

    fun getCostMeeting(costType: Int) {
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
            appSettingRepository.userPreferencesFlow.collect { userDocumentID ->
                val destination = when {
                    !isLogin.value -> MoveMeetingNavi.GO_MEETING_INFO
                    userDocumentID == meetingUiState.hostUserDocumentID -> MoveMeetingNavi.GO_MEETING_MANAGEMENT
                    else -> MoveMeetingNavi.GO_MEETING_INFO
                }
                _goMeetingNavi.emit(Pair(destination, meetingUiState.meetingDocumentID))
            }
        }
    }

    fun getSelectIndex(): Int {
        return selectIndex
    }

    fun setSelectIndex(position: Int) {
        selectIndex = position
    }

    companion object {
        private const val DEFAULT_INDEX = 0
        private const val DEFAULT_COST_TYPE = -1
    }

}