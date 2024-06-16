package com.bestapp.zipbab.ui.meetingmanagement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MeetingManagementViewModel @Inject constructor(
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


    private val _event = MutableSharedFlow<Pair<MoveNavigation, String>>(replay = 0)
    val event: SharedFlow<Pair<MoveNavigation, String>>
        get() = _event


    private val _users = MutableStateFlow<List<UserUiState>>(emptyList())
    val users: StateFlow<List<UserUiState>>
        get() = _users

    private var meetingDocumentId = ""
    private var userDocumentId = ""
    private var isLogin = false
    var hostDocumentId = ""

    init {
        savedStateHandle.get<String>("meetingDocumentID")?.let {
            meetingDocumentId = it
        }

        viewModelScope.launch {
            runCatching {
                meetingRepository.getMeeting(meetingDocumentId)
            }.onSuccess {
                _meeting.emit(it.toUiState())
                getHostImage(it.toUiState())
                checkLogin()
                hostDocumentId = it.toUiState().hostUserDocumentID
                getMember(it.toUiState().members)
            }
        }
    }

    private fun getHostImage(meetingUiState: MeetingUiState) {
        viewModelScope.launch {
            runCatching {
                userRepository.getUser(meetingUiState.hostUserDocumentID)
            }.onSuccess {
                _hostUser.emit(it.toUiState())
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

    private fun getMember(userDocumentIds: List<String>) {
        viewModelScope.launch {
            runCatching {
                userDocumentIds.map {
                    userRepository.getUser(it).toUiState()
                }
            }.onSuccess {
                _users.emit(it)
            }
        }
    }

    fun endMeeting() {
        viewModelScope.launch {
            meetingRepository.endMeeting(meetingDocumentId)
        }
    }

    fun bottomBtnEvent(moveNavigation: MoveNavigation) {
        viewModelScope.launch {
            if (isLogin) {
                _event.emit(Pair(moveNavigation, meetingDocumentId))
            } else {
                _event.emit(Pair(MoveNavigation.GO_LOGIN, meetingDocumentId))
            }
        }
    }
}






