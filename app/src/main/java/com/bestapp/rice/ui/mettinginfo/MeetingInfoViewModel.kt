package com.bestapp.rice.ui.mettinginfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MeetingInfoViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meeting = MutableSharedFlow<MeetingUiState>(replay = 0)
    val meeting: SharedFlow<MeetingUiState>
        get() = _meeting

    private val _hostUser = MutableSharedFlow<UserUiState>(replay = 0)
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

        viewModelScope.launch {
            runCatching {
                meetingRepository.getMeeting(meetingDocumentId)
            }.onSuccess {
                if(it.members.contains(meetingDocumentId)){
                    _isPossible.emit(false)
                }else{
                    _isPossible.emit(true)
                }
                _meeting.emit(it.toUiState())
                getHostImage(it.toUiState())
                checkLogin()
            }
        }
    }

    fun getHostImage(meetingUiState: MeetingUiState){
        viewModelScope.launch {
            runCatching {
                userRepository.getUser(meetingUiState.host)
            }.onSuccess {
                _hostUser.emit(it.toUiState())
                hostDocumentId = it.toUiState().userDocumentID
            }
        }
    }

    fun checkLogin() {
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


    fun btnEvent(){
        viewModelScope.launch {
            if(isLogin){
                _event.emit(Event.JOIN_MEETING)
            }else{
                _event.emit(Event.GO_EVENT)
            }
        }
    }

    fun addPendingMember(){
        viewModelScope.launch {
            meetingRepository.addPendingMember(meetingDocumentId, userDocumentId)
        }
    }
}