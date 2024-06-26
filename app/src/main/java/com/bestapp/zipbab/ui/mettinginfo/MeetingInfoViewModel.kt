package com.bestapp.zipbab.ui.mettinginfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.model.UserUiState
import com.bestapp.zipbab.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingInfoViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meeting = MutableSharedFlow<MeetingUiState>(replay = 1)
    val meeting: SharedFlow<MeetingUiState>
        get() = _meeting

    private val _hostUser = MutableSharedFlow<UserUiState>(replay = 1)
    val hostUser: SharedFlow<UserUiState>
        get() = _hostUser

    private val _isPossible = MutableStateFlow<Boolean>(false)
    val isPossible: StateFlow<Boolean>
        get() = _isPossible

    private val _event = MutableSharedFlow<Event>(replay = 0)
    val event: SharedFlow<Event>
        get() = _event

    private var meetingDocumentId = ""
    private var userDocumentId = ""
    private var isLogin = false
    var hostDocumentId = ""

    init {
        savedStateHandle.get<String>("meetingDocumentId")?.let {
            meetingDocumentId = it
        }
        checkLogin()
        viewModelScope.launch {
            runCatching {
                meetingRepository.getMeeting(meetingDocumentId)
            }.onSuccess {
                if (it.members.contains(userDocumentId)) {
                    _isPossible.emit(false)
                } else {
                    _isPossible.emit(true)
                }
                _meeting.emit(it.toUiState())
                getHostImage(it.toUiState())
            }
        }
    }

    private fun getHostImage(meetingUiState: MeetingUiState) {
        viewModelScope.launch {
            runCatching {
                userRepository.getUser(meetingUiState.hostUserDocumentID)
            }.onSuccess {
                _hostUser.emit(it.toUiState())
                hostDocumentId = it.toUiState().userDocumentID
            }
        }
    }

    private fun checkLogin() {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collect {
                if (it.isNotEmpty()) {
                    userDocumentId = it
                    isLogin = true
                } else {
                    isLogin = false
                }
            }
        }
    }


    fun btnEvent() {
        viewModelScope.launch {
            //TODO(알람 로직 추가)
            if (isLogin) {
                _event.emit(Event.JOIN_MEETING)
            } else {
                _event.emit(Event.GO_EVENT)
            }
        }
    }


    fun addPendingMember() {
        viewModelScope.launch {
            meetingRepository.addPendingMember(meetingDocumentId, userDocumentId)
        }
    }

    fun getMeetingDocumentId(): String {
        return meetingDocumentId
    }

    fun addNotifyList(udi: String, notifyType: NotificationTypeResponse.UserResponseNotification) = viewModelScope.launch { //데이터 추가용 함수
        val result = userRepository.getUser(udi).notificationList
        val list = (result + notifyType) as ArrayList<NotificationTypeResponse.UserResponseNotification>
        userRepository.addNotifyListInfo(udi, list)
    }

    private val _argument = MutableLiveData<Pair<String, String>>()
    val argument : LiveData<Pair<String, String>> = _argument

    fun getUserArgument() = viewModelScope.launch {
        appSettingRepository.userPreferencesFlow.collect { id ->
            val result = userRepository.getUser(id)
            _argument.value = Pair(id, result.nickname)
        }
    }
}