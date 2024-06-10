package com.bestapp.rice.ui.mettinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingListViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _meetingListUiState = MutableStateFlow<MeetingListUiState>(MeetingListUiState())
    val meetingListUiState: StateFlow<MeetingListUiState>
        get() = _meetingListUiState

    private val _userDocumentID = MutableStateFlow<String>("")
    val userDocumentID get() = _userDocumentID.asStateFlow()

    fun setUserDocumentID() {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collect { userDocumentID ->
                // TODO: userDocumentID 값이 비어있어 로그인, 프로필 화면 연동 전까지 ifEmpty 임시 사용
                val userDocumentID = userDocumentID.ifEmpty {
                    BASE_USER_DOCUMENT_ID
                }

                _userDocumentID.value = userDocumentID
                Log.e("userDocumentID", userDocumentID)
            }
        }
    }

    fun getMeetingByUserDocumentID(userDocumentID: String) = viewModelScope.launch {
        val meetings = meetingRepository.getMeetingByUserDocumentID(userDocumentID)

        _meetingListUiState.value = MeetingListUiState(
            meetingUis = meetings.map {
                // TODO: UserRepository 내부에 메서드 추가후 연동, fun getReviewState(userDocumentID: String): Boolean
                val isDoneReview = true // userRepository.getReviewState(userDocumentID)
                val isHost = (it.hostUserDocumentID == userDocumentID)

                it.toMeetingListUi(isDoneReview, isHost)
            }
        )
    }

    companion object {
        private val BASE_USER_DOCUMENT_ID = "yUKL3rt0geiVdQJMOeoF"
    }
}