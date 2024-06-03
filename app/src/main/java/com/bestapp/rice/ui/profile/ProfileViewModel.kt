package com.bestapp.rice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.ImageUiState
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
) : ViewModel() {

    private val _userUiState = MutableStateFlow(UserUiState.Empty)
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    private val _isSelfProfile = MutableStateFlow(false)
    val isSelfProfile: StateFlow<Boolean> = _isSelfProfile.asStateFlow()

    private val _profileUiState = MutableSharedFlow<ImageUiState>()
    val profileUiState: SharedFlow<ImageUiState> = _profileUiState.asSharedFlow()


    init {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow
        }
    }
    fun loadUserInfo(userDocumentId: String) {
        viewModelScope.launch {
            runCatching {
                val userUiState = UserUiState.createFrom(userRepository.getUser(userDocumentId))
                _userUiState.emit(userUiState)

                val selfUserDocumentId = appSettingRepository.userPreferencesFlow.firstOrNull()
                _isSelfProfile.emit(userUiState.userDocumentID == selfUserDocumentId)
            }
        }
    }

    fun onProfileImageClicked() {
        if (_userUiState.value.profileImage == UserUiState.Empty.profileImage || _userUiState.value.profileImage.url.isBlank()) {
            return
        }
        viewModelScope.launch {
            _profileUiState.emit(_userUiState.value.profileImage)
        }
    }
}