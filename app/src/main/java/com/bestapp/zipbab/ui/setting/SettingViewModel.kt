package com.bestapp.zipbab.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.Privacy
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.SignOutState
import com.bestapp.zipbab.model.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bestapp.zipbab.model.toUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val userUiState: StateFlow<UserUiState> = appSettingRepository.userPreferencesFlow
        .map { userDocumentID ->
            if (userDocumentID.isBlank()) {
                UserUiState()
            } else {
                _userInfoLoadState.emit(LoadingState.OnLoading)
                val user = userRepository.getUser(userDocumentID).toUiState()
                _userInfoLoadState.emit(LoadingState.Done)
                user
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserUiState(),
        )

    private val _message = MutableSharedFlow<SettingMessage>()
    val message: SharedFlow<SettingMessage> = _message.asSharedFlow()

    private val _requestDeleteUrl = MutableStateFlow("")
    val requestDeleteUrl: StateFlow<String> = _requestDeleteUrl.asStateFlow()

    private val _requestPrivacyUrl = MutableStateFlow(Privacy())
    val requestPrivacyUrl: StateFlow<Privacy> = _requestPrivacyUrl.asStateFlow()

    private val _requestLocationPolicyUrl = MutableStateFlow(Privacy())
    val requestLocationPolicyUrl: StateFlow<Privacy> = _requestLocationPolicyUrl.asStateFlow()

    private val _userInfoLoadState = MutableStateFlow<LoadingState>(LoadingState.Default)
    val userInfoLodeState: StateFlow<LoadingState> = _userInfoLoadState.asStateFlow()

    fun init() {
        viewModelScope.launch {
            _requestDeleteUrl.emit(appSettingRepository.getDeleteRequestUrl())
            _requestPrivacyUrl.emit(appSettingRepository.getPrivacyInfo())
            _requestLocationPolicyUrl.emit(appSettingRepository.getLocationPolicyInfo())
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                appSettingRepository.removeUserDocumentId()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                val userDocumentID = userUiState.firstOrNull()?.userDocumentID ?: return@runCatching
                val signOutState = userRepository.signOutUser(userDocumentID).toUiState()
                when (signOutState) {
                    SignOutState.Fail -> _message.emit(SettingMessage.SIGN_OUT_FAIL)
                    SignOutState.IsNotAllowed -> _message.emit(SettingMessage.SIGN_OUT_IS_NOT_ALLOWED)
                    SignOutState.Success -> appSettingRepository.removeUserDocumentId()
                }
            }.onFailure {
                throw it
            }
        }
    }
}