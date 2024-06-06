package com.bestapp.rice.ui.meetingmanagement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.network.FirebaseClient
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.AppSettingRepositoryImpl
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.MeetingRepositoryImpl
import com.bestapp.rice.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MeetingManagementViewModel(
    private val appSettingRepository: AppSettingRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _meetingManagementUiState =
        MutableStateFlow<List<MeetingManagementUiState>>(emptyList())
    val meetingManagementUiState: StateFlow<List<MeetingManagementUiState>>
        get() = _meetingManagementUiState

    fun getMeetingByUserDocumentID() = viewModelScope.launch {
        appSettingRepository.userPreferencesFlow.collect { userDocumentID ->
            val meetings = meetingRepository.getMeetingByUserDocumentID(
                userDocumentID = if (userDocumentID.isNotEmpty()) {
                    userDocumentID
                } else {
                    "yUKL3rt0geiVdQJMOeoF"
                }
            )

            _meetingManagementUiState.value = meetings.map {
                it.createFrom()
            }
        }
    }
}

class MeetingManagementViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appSettingRepository: AppSettingRepository =
        AppSettingRepositoryImpl(context.dataStore, FirebaseClient.privacyStoreService)
    private val meetingRepository: MeetingRepository =
        MeetingRepositoryImpl(FirebaseClient.store, FirebaseClient.meetingStoreService)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MeetingManagementViewModel::class.java -> MeetingManagementViewModel(
                appSettingRepository, meetingRepository
            )

            else -> throw IllegalArgumentException()
        } as T
    }
}