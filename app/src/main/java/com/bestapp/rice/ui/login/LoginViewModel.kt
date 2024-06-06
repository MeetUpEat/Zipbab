package com.bestapp.rice.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository
): ViewModel() {
    private val _login = MutableLiveData<Boolean>()
    val login : LiveData<Boolean> = _login

    fun loginCompare(id: String, password: String) = viewModelScope.launch {
        userRepository.login(id = id, pw = password)
    }

    fun loginSave(id: String) = viewModelScope.launch {
        appSettingRepository.saveId(id = id)
    }
}