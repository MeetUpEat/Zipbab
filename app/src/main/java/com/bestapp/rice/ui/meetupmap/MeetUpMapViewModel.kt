package com.bestapp.rice.ui.meetupmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.model.args.toUi
import com.bestapp.rice.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetUpMapViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _meetingUiState = MutableStateFlow<MeetingUiState>(MeetingUiState())
    val meetingUiState: SharedFlow<MeetingUiState> = _meetingUiState.asStateFlow()

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState())
    val userUiState: SharedFlow<UserUiState> = _userUiState.asStateFlow()

    private val _meetUpMapUiState = MutableStateFlow<MeetUpMapUiState>(MeetUpMapUiState())
    val meetUpMapUiState: SharedFlow<MeetUpMapUiState> = _meetUpMapUiState.asStateFlow()

    fun getMeeting(meetingDocumentID: String) = viewModelScope.launch {
        val meetingDocumentID = meetingDocumentID.ifEmpty {
            DEFAULT_MEETING_DOCUMENT_ID
        }

        val meeting = meetingRepository.getMeeting(meetingDocumentID)[0]
        _meetingUiState.emit(meeting.toUiState())
    }

    fun getUsers(userDocumentIDs: List<String>) = viewModelScope.launch {
        val meetUpMapUiState = MeetUpMapUiState(
            meetUpMapUserUis = userDocumentIDs.map {
                userRepository.getUser(it).toUiState().toMeetUpMapUi()
            },
        )

        _meetUpMapUiState.emit(meetUpMapUiState)
    }

    companion object {
        val DEFAULT_MEETING_DOCUMENT_ID = "O84eyapKdqIgbjitZZIr"
        val DEFAULT_USER_DOCUMENT_ID = "yUKL3rt0geiVdQJMOeoF"
    }
}