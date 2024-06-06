package com.bestapp.rice.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import com.bestapp.rice.model.toUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val userUiState: StateFlow<UserUiState> = appSettingRepository.userPreferencesFlow
        .map { userDocumentId ->
            if (userDocumentId.isBlank()) {
                UserUiState()
            } else {
                userRepository.getUser(userDocumentId).toUiState()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserUiState(),
        )

    private val _message = MutableSharedFlow<SettingMessage>()
    val message: SharedFlow<SettingMessage> = _message.asSharedFlow()

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
                val userDocumentId = userUiState.firstOrNull()?.userDocumentID ?: return@runCatching
                val isSuccess = userRepository.signOutUser(userDocumentId)
                if (isSuccess) {
                    appSettingRepository.removeUserDocumentId()
                } else {
                    _message.emit(SettingMessage.SIGN_OUT_FAIL)
                }
            }
        }
    }
}