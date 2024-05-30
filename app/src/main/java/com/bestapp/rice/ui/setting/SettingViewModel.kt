package com.bestapp.rice.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val appSettingRepository: AppSettingRepository,
) : ViewModel() {

    private val _userUiState = MutableSharedFlow<UserUiState>(replay = 1)
    val userUiState: SharedFlow<UserUiState>
        get() = _userUiState

    fun getUserInfo() {
        viewModelScope.launch {
            runCatching {
                val response = appSettingRepository.getUserInfo()
                val data = UserUiState.createFrom(response)
                _userUiState.emit(data)
            }
        }
    }
}