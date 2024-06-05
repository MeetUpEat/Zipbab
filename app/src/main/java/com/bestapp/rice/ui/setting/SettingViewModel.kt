package com.bestapp.rice.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userUiState = MutableSharedFlow<UserUiState>(replay = 1)
    val userUiState: SharedFlow<UserUiState> = _userUiState.asSharedFlow()

    private val _message = MutableSharedFlow<SettingMessage>()
    val message: SharedFlow<SettingMessage> = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            appSettingRepository.userPreferencesFlow.collectLatest { userDocumentId ->
                val userUiState = if (userDocumentId.isBlank()) {
                    UserUiState.Empty
                } else {
                    UserUiState.createFrom(userRepository.getUser(userDocumentId))
                }
                _userUiState.emit(userUiState)
            }
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
                val userState = _userUiState.firstOrNull()?.toData() ?: return@runCatching
                val isSuccess = userRepository.signOut(userState)
                if (isSuccess) {
                    appSettingRepository.removeUserDocumentId()
                } else {
                    _message.emit(SettingMessage.SIGN_OUT_FAIL)
                }
            }
        }
    }
}