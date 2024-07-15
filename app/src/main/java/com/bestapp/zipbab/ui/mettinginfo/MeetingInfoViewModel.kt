package com.bestapp.zipbab.ui.mettinginfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.NotificationType
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.model.toMemberInfo
import com.bestapp.zipbab.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingInfoViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    appSettingRepository: AppSettingRepository,
) : ViewModel() {

    private val userDocumentID = appSettingRepository.userDocumentID
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = "",
        )

    private val _meeting = MutableStateFlow(MeetingUiState())
    val meeting: StateFlow<MeetingUiState> = _meeting.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.NotYet)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    val isHostMeeting = combine(userDocumentID, _meeting) { userDocumentId, meeting ->
        if (userDocumentId == meeting.hostUserDocumentID) {
            return@combine true
        }

        _registerState.value = if (meeting.members.contains(userDocumentId)) {
            RegisterState.Joined
        } else if (meeting.pendingMembers.contains(userDocumentId)) {
            RegisterState.Requested
        } else {
            RegisterState.NotYet
        }

        return@combine false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false,
    )

    private val _event = MutableStateFlow<MeetingInfoEvent>(MeetingInfoEvent.Default)
    val event: StateFlow<MeetingInfoEvent> = _event.asStateFlow()

    private val _members = MutableStateFlow<List<MemberInfo>>(emptyList())
    val members: StateFlow<List<MemberInfo>> = _members.asStateFlow()

    fun onRegister() {
        if (_event.value == MeetingInfoEvent.RegisterInProgress) {
            return
        }
        _event.value = MeetingInfoEvent.RegisterInProgress

        viewModelScope.launch {
            runCatching {
                val isSuccess = meetingRepository.addPendingMember(
                    _meeting.value.meetingDocumentID,
                    userDocumentID.value
                )
                if (isSuccess.not()) {
                    _event.emit(MeetingInfoEvent.RegisterFail)
                    return@launch
                }
                setMeetingDocumentId(_meeting.value.meetingDocumentID)
                _event.emit(MeetingInfoEvent.RegisterSuccess)

                // 알림 목록에 등록
                val isDone = userRepository.addNotification(
                    type = NotificationType.REGISTER_MEETING,
                    userDocumentID = userDocumentID.value,
                    meetingDocumentID = _meeting.value.meetingDocumentID,
                    hostDocumentID = _meeting.value.hostUserDocumentID,
                )

                if (isDone.not()) {
                    // TODO : 알림 목록 등록에 실패한 경우, WorkManager에 다시 알림 등록을 시도하도록 요청하기?
                }
            }
        }
    }

    fun onEventConsumed() {
        _event.value = MeetingInfoEvent.Default
    }

    fun setMeetingDocumentId(meetingDocumentId: String) {
        viewModelScope.launch {
            runCatching {
                meetingRepository.getMeeting(meetingDocumentId).toUiState()
            }.onSuccess { state ->
                _meeting.emit(state)
                fetchUserInfo(state.hostUserDocumentID, state.members)
            }
        }
    }

    private fun fetchUserInfo(hostDocumentId: String, memberIDs: List<String>) {
        viewModelScope.launch {
            runCatching {
                val users = mutableListOf(userRepository.getUser(hostDocumentId).toUiState().toMemberInfo(isHost = true))

                for (memberID in memberIDs) {
                    users.add(userRepository.getUser(memberID).toUiState().toMemberInfo(isHost = false))
                }
                _members.emit(users)
            }
        }
    }

    fun onMeetingEnd() {
        viewModelScope.launch {
            runCatching {
                val isDone = meetingRepository.endMeeting(_meeting.value.meetingDocumentID)
                if (isDone) {
                    _event.emit(MeetingInfoEvent.EndMeetingSuccess)
                } else {
                    _event.emit(MeetingInfoEvent.EndMeetingFail)
                }
            }
        }
    }
}