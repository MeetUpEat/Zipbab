package com.bestapp.rice.ui.mettinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private var userDocumentID = ""

    fun getUserDocumentID(): String {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collect { userDocumentID ->
                this@MeetingListViewModel.userDocumentID = userDocumentID
            }
        }

        // TODO: 연동 후, ifEmpty 코드 제거해야함
        return userDocumentID.ifEmpty {
            BASE_USER_DOCUMENT_ID
        }
    }

    fun getMeetingByUserDocumentID(userDocumentID: String) = viewModelScope.launch {
        val meetings = meetingRepository.getMeetingByUserDocumentID(
            userDocumentID = userDocumentID
        )

        _meetingListUiState.value = MeetingListUiState(
            meetingUis = meetings.map {
                // TODO: UserRepository 내부에 메서드 추가후 연동, fun getReviewState(userDocumentID: String): Boolean
                val isDoneReview = true // userRepository.getReviewState(userDocumentID)

                it.toUi(isDoneReview)
            }
        )
    }

    companion object {
        private val BASE_USER_DOCUMENT_ID = "yUKL3rt0geiVdQJMOeoF"
    }
}