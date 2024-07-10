package com.bestapp.zipbab.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.Privacy
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val signUpInputValidator: SignUpInputValidator,
) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Default)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    private val _requestPrivacyUrl = MutableStateFlow(Privacy())
    val requestPrivacyUrl: StateFlow<Privacy> = _requestPrivacyUrl.asStateFlow()

    private val isTermAgreed = MutableStateFlow(false)
    private val nickname = MutableStateFlow("")
    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val passwordCompare = MutableStateFlow("")
    private val isTermChecked = MutableStateFlow(false)

    val inputValidation = combine(
        nickname,
        email,
        password,
        passwordCompare,
        isTermChecked
    ) { nickname, email, password, passwordCompare, isTermChecked ->
        signUpInputValidator.isValid(nickname, email, password, passwordCompare, isTermChecked)
    }

    init {
        viewModelScope.launch {
            _requestPrivacyUrl.emit(appSettingRepository.getPrivacyInfo())
        }
    }

    fun onSignUp() {
        viewModelScope.launch {
            val signUpState =
                userRepository.signUpUser(nickname.value, email.value, password.value).toUi()

            when (signUpState) {
                SignUpState.Default -> Unit
                SignUpState.DuplicateEmail -> Unit
                SignUpState.Fail -> Unit
                is SignUpState.Success -> {
                    appSettingRepository.updateUserDocumentId(signUpState.userDocumentID)
                }
            }
            _signUpState.emit(signUpState)
        }
    }

    fun updateNickname(nickname: String) {
        this.nickname.value = nickname
    }

    fun onTermClick(isClicked: Boolean) {
        isTermChecked.value = isClicked
    }

    fun updateEmail(email: String) {
        this.email.value = email.trim()
    }

    fun updatePassword(password: String) {
        this.password.value = password
    }

    fun updatePasswordCompare(passwordCheck: String) {
        this.passwordCompare.value = passwordCheck
    }

    fun updateTermsAgree(newState: Boolean) {
        isTermAgreed.value = newState
    }

    fun resetSignUpState() {
        _signUpState.value = SignUpState.Default
    }
}