package com.bestapp.rice.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.model.MeetingUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SearchViewModel(
    private val meetingRepositoryImpl: MeetingRepository,
    private val appSettingRepositoryImpl: AppSettingRepository
) : ViewModel() {

    private val _searchMeeting = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val searchMeeting: StateFlow<List<MeetingUiState>>
        get() = _searchMeeting

    private val _goDirection = MutableSharedFlow<GoDirection>()
    val goDirection: SharedFlow<GoDirection>
        get() = _goDirection


    private var meetingDocumentId = ""

    fun requestSearch(query: String) {
        viewModelScope.launch {
            runCatching {
                meetingRepositoryImpl.getSearch(query)
            }.onSuccess {
                val meetingUiStateList = it.map { meeting ->
                    MeetingUiState.createFrom(meeting)
                }
                _searchMeeting.value = meetingUiStateList
            }
        }
    }

    fun goDetailMeeting(meetingUiState: MeetingUiState) {
        viewModelScope.launch {
            runCatching {
                appSettingRepositoryImpl.getUserInfo()
            }.onSuccess {
                if (it.userDocumentID.isEmpty()) {
                    _goDirection.emit(GoDirection.GO_LOGIN)
                } else if (it.userDocumentID == meetingUiState.host) {
                    _goDirection.emit(GoDirection.GO_MEETING_MANAGEMENT)
                } else {
                    _goDirection.emit(GoDirection.GO_MEETING_INFO)
                }
            }
        }
    }

    fun setMeetingDocumentId(meetingDocumentId: String) {
        this.meetingDocumentId = meetingDocumentId
    }

    fun getMeetingDocumentId(): String {
        return this.meetingDocumentId
    }

}