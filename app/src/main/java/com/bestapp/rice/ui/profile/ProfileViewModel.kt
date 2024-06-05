package com.bestapp.rice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.model.toUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
) : ViewModel() {

    private val _userUiState = MutableStateFlow(UserUiState.Empty)
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    private val _isSelfProfile = MutableStateFlow(false)
    val isSelfProfile: StateFlow<Boolean> = _isSelfProfile.asStateFlow()

    private val _profileUiState = MutableSharedFlow<String>()
    val profileUiState: SharedFlow<String> = _profileUiState.asSharedFlow()

    fun loadUserInfo(userDocumentId: String) {
        viewModelScope.launch {
            runCatching {
                val userUiState = userRepository.getUser(userDocumentId).toUiState()
                _userUiState.emit(userUiState)

                appSettingRepository.userPreferencesFlow.collect { selfId ->
                    _isSelfProfile.emit(selfId == userUiState.userDocumentID)
                }
            }
        }
    }

    fun onProfileImageClicked() {
        if (_userUiState.value.profileImage == UserUiState.Empty.profileImage || _userUiState.value.profileImage.isBlank()) {
            return
        }
        viewModelScope.launch {
            _profileUiState.emit(_userUiState.value.profileImage)
        }
    }
}