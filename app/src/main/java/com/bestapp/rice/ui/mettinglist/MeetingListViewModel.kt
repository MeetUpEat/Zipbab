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

    private val _meetingListUiState =
        MutableStateFlow<List<MeetingListUiState>>(emptyList())
    val meetingListUiState: StateFlow<List<MeetingListUiState>>
        get() = _meetingListUiState

    fun getMeetingByUserDocumentID() = viewModelScope.launch {
        appSettingRepository.userPreferencesFlow.collect { userDocumentID ->
            val meetings = meetingRepository.getMeetingByUserDocumentID(
                userDocumentID = if (userDocumentID.isNotEmpty()) {
                    userDocumentID
                } else {
                    "yUKL3rt0geiVdQJMOeoF"
                }
            )

            _meetingListUiState.value = meetings.map {
                // TODO: UserRepository 내부에 메서드 추가후 연동, fun getReviewState(userDocumentID: String): Boolean
                val isDoneReview = false // userRepository.getReviewState(userDocumentID)

                it.createFrom(isDoneReview)
            }
        }
    }
}