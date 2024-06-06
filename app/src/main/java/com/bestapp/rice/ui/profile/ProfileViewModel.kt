package com.bestapp.rice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
) : ViewModel() {

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    fun loadUserInfo(userDocumentId: String) {
        viewModelScope.launch {
            runCatching {
                val userUiState = userRepository.getUser(userDocumentId).toUiState()

                appSettingRepository.userPreferencesFlow.collect { selfDocumentId ->
                    _profileUiState.emit(
                        ProfileUiState(
                            userDocumentID = userUiState.userDocumentID,
                            nickname = userUiState.nickname,
                            profileImage = userUiState.profileImage,
                            temperature = userUiState.temperature,
                            meetingCount = userUiState.meetingCount,
                            postUiStates = userUiState.postUiStates,
                            isSelfProfile = userUiState.userDocumentID == selfDocumentId,
                            isProfileClicked = false,
                        )
                    )
                }
            }
        }
    }

    fun onProfileImageClicked() {
        if (_profileUiState.value.profileImage.isBlank()) {
            return
        }
        viewModelScope.launch {
            _profileUiState.emit(_profileUiState.value.copy(isProfileClicked = true))
        }
    }

    fun closeLargeProfile() {
        viewModelScope.launch {
            _profileUiState.emit(_profileUiState.value.copy(isProfileClicked = false))
        }
    }
}