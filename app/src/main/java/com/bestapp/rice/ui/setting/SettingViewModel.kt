package com.bestapp.rice.ui.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    fun getUserInfo() {
        viewModelScope.launch {
            val userDocumentId = appSettingRepository.userPreferencesFlow.firstOrNull() ?: run {
                _userUiState.emit(UserUiState.Empty)
                Log.i("TEST", "empty")
                return@launch
            }
            val user = userRepository.getUser(userDocumentId)
            Log.i("TEST", user.toString())
            val data = UserUiState.createFrom(user)
            _userUiState.emit(data)
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                appSettingRepository.removeUserDocumentId()
                _userUiState.emit(UserUiState.Empty)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching {
                val userState = _userUiState.firstOrNull()?.toData() ?: return@runCatching
                userRepository.signOutUser(userState.userDocumentID)
            }
        }
    }

    fun tempLogin() {
        viewModelScope.launch {
            val userDocumentId = "Vnwd46RdvGT3RFDftKIm"
//            _userUiState.emit(user)
            appSettingRepository.updateUserDocumentId(userDocumentId)
        }

    }
}