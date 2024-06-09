package com.bestapp.rice.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository
): ViewModel() {
    private val _isSignUpState = MutableLiveData<Pair<String, Boolean>>()
    val isSignUpState : LiveData<Pair<String, Boolean>> = _isSignUpState

    fun userDataSave(user: User) = viewModelScope.launch {
        val result = userRepository.signUpUser(user)

        _isSignUpState.value = result
    }

    fun saveDocumentId(documentId: String) = viewModelScope.launch {
        appSettingRepository.updateUserDocumentId(documentId)
    }
}