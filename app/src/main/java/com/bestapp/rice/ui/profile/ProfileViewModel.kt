package com.bestapp.rice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
): ViewModel() {

    private val _userUiState = MutableStateFlow(UserUiState.Empty)
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    fun loadUserInfo(userDocumentId: String) {
        viewModelScope.launch {
            runCatching {
                val userUiState = UserUiState.createFrom(userRepository.getUser(userDocumentId))
                _userUiState.emit(userUiState)
            }
        }
    }


}