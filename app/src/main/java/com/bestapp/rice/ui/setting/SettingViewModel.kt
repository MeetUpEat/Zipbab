package com.bestapp.rice.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingViewModel(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userUiState = MutableSharedFlow<UserUiState>(replay = 1)
    val userUiState: SharedFlow<UserUiState>
        get() = _userUiState

    private val _message = MutableSharedFlow<SettingMessage>()
    val message: SharedFlow<SettingMessage>
        get() = _message

    fun getUserInfo() {
        viewModelScope.launch {
            runCatching {
                val response = appSettingRepository.getUserInfo()
                val data = UserUiState.createFrom(response)
                _userUiState.emit(data)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                val isSuccess = appSettingRepository.removeUserInfo()
                if (isSuccess) {
                    _userUiState.emit(UserUiState.Empty)
                } else {
                    _message.emit(SettingMessage.LOGOUT_FAIL)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                val userState = _userUiState.firstOrNull()?.toData() ?: return@runCatching
                val isSuccess = userRepository.signOut(userState)
                if (isSuccess) {
                    _userUiState.emit(UserUiState.Empty)
                } else {
                    _message.emit(SettingMessage.SIGN_OUT_FAIL)
                }
            }
        }
    }
}