package com.bestapp.zipbab.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.model.MeetingUiState
import com.bestapp.zipbab.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _searchMeeting = MutableStateFlow<List<MeetingUiState>>(emptyList())
    val searchMeeting: StateFlow<List<MeetingUiState>> = _searchMeeting.asStateFlow()

    fun requestSearch(query: String) {
        viewModelScope.launch {
            runCatching {
                meetingRepository.getSearch(query)
            }.onSuccess {
                val meetingUiStateList = it.map { meeting ->
                    meeting.toUiState()
                }
                _searchMeeting.value = meetingUiStateList
            }
        }
    }
}