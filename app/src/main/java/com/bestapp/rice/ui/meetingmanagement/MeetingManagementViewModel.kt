package com.bestapp.rice.ui.meetingmanagement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.model.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bestapp.rice.model.toUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingManagementViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meeting = MutableSharedFlow<MeetingUiState>(replay = 0)
    val meeting: SharedFlow<MeetingUiState>
        get() = _meeting

    private val _hostImg = MutableSharedFlow<String>(replay = 0)
    val hostImg: SharedFlow<String>
        get() = _hostImg


    private val _event = MutableSharedFlow<Pair<MoveNavigation, String>>(replay = 0)
    val event: SharedFlow<Pair<MoveNavigation, String>>
        get() = _event


    private val _users = MutableStateFlow<List<UserUiState>>(emptyList())
    val users: StateFlow<List<UserUiState>>
        get() = _users

    var meetingDocumentId = ""
    var userDocumentId = ""
    var isLogin = false

    init {
        savedStateHandle.get<String>("meetingDocumentId")?.let {
            meetingDocumentId = it
        }

        viewModelScope.launch {
            runCatching {
                meetingRepository.getMeeting(meetingDocumentId)
            }.onSuccess {
                _meeting.emit(it.toUiState())
                getHostImage(it.toUiState())
                checkLogin()
                getMembser(it.toUiState().members)
            }
        }
    }

    private fun getHostImage(meetingUiState: MeetingUiState) {
        viewModelScope.launch {
            runCatching {
                userRepository.getUser(meetingUiState.host)
            }.onSuccess {
                _hostImg.emit(it.profileImage)
            }
        }
    }

    private fun checkLogin() {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collect {
                if (it.isEmpty()) {
                    isLogin = false
                } else {
                    userDocumentId = it
                    isLogin = true

                }
            }
        }
    }

    private fun getMembser(userDocumentIds: List<String>){
        viewModelScope.launch {
            runCatching {
                userDocumentIds.map {
                    userRepository.getUser(it).toUiState()
                }.toList()
            }.onSuccess {
                _users.emit(it)
            }
        }
    }

    fun endMeeting(){
        viewModelScope.launch {
            meetingRepository.endMeeting(meetingDocumentId)
        }
    }

    fun btnEvent(moveNavigation: MoveNavigation) {
        viewModelScope.launch {
            if (isLogin) {
                _event.emit(Pair(moveNavigation, meetingDocumentId))
            } else {
                _event.emit(Pair(MoveNavigation.GO_LOGIN, meetingDocumentId))
            }
        }
    }
}






