package com.bestapp.zipbab.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.PlaceLocation
import com.bestapp.zipbab.data.model.remote.User
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository
): ViewModel() {
    private val _isSignUpState = MutableLiveData<String>()
    val isSignUpState : LiveData<String> = _isSignUpState

    private val rand = Random(System.currentTimeMillis())
    private val emailRegex = """^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{3,})$"""

    private val nickname = MutableLiveData<String>()
    private val email = MutableLiveData<String>()

    private val password = MutableLiveData<String>()
    private val passwordCheck = MutableLiveData<String>()
    private val isTermChecked = MutableLiveData<Boolean>()

    private val _nicknameValid = MediatorLiveData<Boolean>().apply {
        addSource(nickname) {
            this.value = it.length in 3..5
        }
    }
    val nicknameValid: LiveData<Boolean>
        get() = _nicknameValid

    private val _emailValid = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            this.value = Pattern.matches(emailRegex, it)
        }
    }
    val emailValid: LiveData<Boolean>
        get() = _emailValid

    private val _passwordValid = MediatorLiveData<Boolean>().apply {
        addSource(password) { password ->
            this.value = password.length in 4..10 && password == passwordCheck.value
        }
        addSource(passwordCheck) { passWordCheck ->
            this.value = passWordCheck.length in 4..10 && password.value == passWordCheck
        }
    }
    val passwordValid: LiveData<Boolean>
        get() = _passwordValid

    private val _isAllValid = MediatorLiveData<SignUpValidState>().apply {
        listOf(_nicknameValid, _emailValid, _passwordValid, isTermChecked).forEach {
            addSource(it) {
                this.value = checkAllValid()
            }
        }
    }
    val isAllValid: LiveData<SignUpValidState>
        get() = _isAllValid

    private fun checkAllValid(): SignUpValidState {
        if (_nicknameValid.value != true) {
            return SignUpValidState.None
        }
        if (_emailValid.value != true) {
            return SignUpValidState.UntilNickname
        }
        if (_passwordValid.value != true) {
            return SignUpValidState.UntilEmail
        }
        if (isTermChecked.value != true) {
            return SignUpValidState.UntilPassWord
        }
        return SignUpValidState.All
    }

    fun userDataSave() {
        val user = User(
            userDocumentID = "",
            uuid = rand.nextInt(1, 1_000_000_000).toString(),
            nickname = nickname.value ?: return,
            id = email.value ?: return,
            pw = password.value ?: return,
            profileImage = "",
            temperature = 36.5,
            meetingCount = 0,
            notificationList = listOf(),
            meetingReviews = listOf(),
            posts = listOf(),
            placeLocation = PlaceLocation(
                locationAddress = "",
                locationLat = "",
                locationLong = ""
            ),
        )
        viewModelScope.launch {
            val result = userRepository.signUpUser(user)
            _isSignUpState.value = result
        }
    }

    fun saveDocumentId(documentId: String) = viewModelScope.launch {
        appSettingRepository.updateUserDocumentId(documentId)
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

    fun updatePasswordCheck(passwordCheck: String) {
        this.passwordCheck.value = passwordCheck
    }
}