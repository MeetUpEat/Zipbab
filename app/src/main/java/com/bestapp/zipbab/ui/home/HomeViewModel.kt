package com.bestapp.zipbab.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.CategoryRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.model.FilterUiState
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val categoryRepository: CategoryRepository,
    private val meetingRepositoryImp: MeetingRepository
) : ViewModel() {

    private val _goNavigate = MutableSharedFlow<MoveNavigate>(replay = 0)
    val goNavigate: SharedFlow<MoveNavigate>
        get() = _goNavigate

    private val _goMyMeeting = MutableSharedFlow<MoveMyMeetingNavigate>(replay = 0)
    val goMyMeeting: SharedFlow<MoveMyMeetingNavigate>
        get() = _goMyMeeting

    private val _foodCategory = MutableStateFlow<List<FilterUiState.FoodUiState>>(emptyList())
    val foodCategory: StateFlow<List<FilterUiState.FoodUiState>>
        get() = _foodCategory

    private val _costCategory = MutableStateFlow<List<FilterUiState.CostUiState>>(emptyList())
    val costCategory: StateFlow<List<FilterUiState.CostUiState>>
        get() = _costCategory

    private val _enterMeeting = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val enterMeeting: StateFlow<List<MeetingUiState>>
        get() = _enterMeeting

    private val _isLogin = MutableSharedFlow<Boolean>(replay = 1)
    val isLogin: SharedFlow<Boolean>
        get() = _isLogin

    private var userDocumentedId = ""
    var meetingDocumentID = ""


    fun checkLogin() {

        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collectLatest {
                if (it.isEmpty()) {
                    _isLogin.emit(false)
                } else {

                    userDocumentedId = it
                    getMeetingByUserDocumentID(userDocumentedId)
                    _isLogin.emit(true)
                }
            }
        }
    }

    fun goNavigate() {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collect {

                if (it.isEmpty()) {
                    _goNavigate.emit(MoveNavigate.GO_LOGIN)
                } else {
                    _goNavigate.emit(MoveNavigate.GO_CREATMEET)
                }
            }
        }
    }

    fun getFoodCategory() {
        viewModelScope.launch {
            runCatching {
                categoryRepository.getFoodCategory()
            }.onSuccess {
                val foodUiStateList = it.map { filter ->
                    filter.toUiState()
                }
                _foodCategory.value = foodUiStateList
            }
        }
    }

    fun getCostCategory() {
        viewModelScope.launch {
            runCatching {
                categoryRepository.getCostCategory()
            }.onSuccess {
                val costUiStateList = it.map { filter ->
                    filter.toUiState()
                }
                _costCategory.value = costUiStateList
            }
        }
    }

    private fun getMeetingByUserDocumentID(userDocumentedId: String) {

        viewModelScope.launch {
            runCatching {
                meetingRepositoryImp.getMeetingByUserDocumentID(userDocumentedId).filter {
                    it.activation
                }
            }.onSuccess {
                val meetingUiStateList = it.map { meeting ->
                    meeting.toUiState()
                }
                _enterMeeting.value = meetingUiStateList
            }
        }
    }


    fun goMyMeeting(meetingUiState: MeetingUiState) {
        this.meetingDocumentID = meetingUiState.meetingDocumentID
        viewModelScope.launch {
            runCatching {
                userDocumentedId == meetingUiState.hostUserDocumentID
            }.onSuccess { isHost ->
                if (isHost) {
                    _goMyMeeting.emit(MoveMyMeetingNavigate.GO_MEETING_MANAGEMENT)
                } else {
                    _goMyMeeting.emit(MoveMyMeetingNavigate.GO_MEETING_INFO)
                }
            }
        }
    }

    private val _state = MutableLiveData<Boolean>()
    val state : LiveData<Boolean> = _state
    fun checkUserState() = viewModelScope.launch{
        appSettingRepository.userPreferencesFlow.collect {
            _state.value = it.isNotEmpty()
        }
    }
}