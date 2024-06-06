package com.bestapp.rice.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.UserUiState
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _userData = MutableLiveData<Boolean>()
    val userData : LiveData<Boolean> = _userData


    fun userDataSave(user: User) = viewModelScope.launch {
        val result = userRepository.signUpUser(user)
        _userData.value = result
    }
}