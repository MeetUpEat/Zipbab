package com.bestapp.rice.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository
): ViewModel() {
    private val _login = MutableLiveData<Boolean>()
    val login : LiveData<Boolean> = _login

    fun loginCompare(id: String, password: String) = viewModelScope.launch {
        val result = userRepository.login(id = id, pw = password)
        _login.value = result
    }

    fun loginSave(id: String) = viewModelScope.launch {
        appSettingRepository.saveId(id = id)
    }

    private val _loginLoad = MutableLiveData<String>()
    val loginLoad : LiveData<String> = _loginLoad

    fun loginLoad() = viewModelScope.launch {
        val result = appSettingRepository.getId()
        _loginLoad.value = result
    }
}