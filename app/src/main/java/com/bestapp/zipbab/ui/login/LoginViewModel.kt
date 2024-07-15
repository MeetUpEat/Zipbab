package com.bestapp.zipbab.ui.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.LoginResult
import com.bestapp.zipbab.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val meetingRepository: MeetingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedID = MutableStateFlow("")
    val savedID: StateFlow<String> = _savedID.asStateFlow()

    private val _isRememberId = MutableStateFlow(false)
    val isRememberId: StateFlow<Boolean> = _isRememberId.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Default)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val inputId = MutableStateFlow("")
    private val inputPw = MutableStateFlow("")

    val inputState = combine(inputId, inputPw) { id, pw ->
        id.isNotBlank() && pw.isNotBlank()
    }


    private var meetingDocumentID = ""
    private var hostDocumentID = ""

    init {
        viewModelScope.launch {
            val rememberId = appSettingRepository.getRememberId()
            _isRememberId.emit(rememberId.isNotBlank())
            _savedID.emit(rememberId)
        }

        savedStateHandle.get<String>("meetingDocumentID")?.let {
            if (it.isNotEmpty()) {
                meetingDocumentID = it
            }
        }
        viewModelScope.launch {
            runCatching {
                meetingRepository.getMeeting(meetingDocumentID)
            }.onSuccess { result ->
                hostDocumentID = result.hostUserDocumentID
            }
        }
    }

    fun updateId(newId: String) {
        inputId.value = newId
    }

    fun updatePw(newPw: String) {
        inputPw.value = newPw
    }

    fun onLogin() {
        if (inputId.value.isBlank() || inputPw.value.isBlank()) {
            return
        }
        viewModelScope.launch {
            runCatching {
                when (val loginResult = userRepository.login(inputId.value, inputPw.value).toUi()) {
                    LoginResult.Fail -> {
                        _loginState.emit(LoginState.Fail)
                    }
                    is LoginResult.Success -> {
                        appSettingRepository.updateUserDocumentId(loginResult.userDocumentID)
                        if (_isRememberId.value) {
                            appSettingRepository.updateRememberId(inputId.value)
                        } else {
                            appSettingRepository.removeRememberId()
                        }
                        _loginState.emit(LoginState.Success)
                    }
                }
            }.onFailure {
                _loginState.emit(LoginState.Fail)
            }
        }
    }

    fun updateIdRemember(check: Boolean) {
        _isRememberId.value = check
    }

    fun idRestored() {
        _savedID.value = ""
    }

    fun onLoginStateUsed() {
        _loginState.value = LoginState.Default
    }
}