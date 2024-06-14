package com.bestapp.rice.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository
): ViewModel() {
    private val _login = MutableLiveData<String>()
    val login : LiveData<String> = _login

    private val _savedID = MutableLiveData<String>()
    val savedID : LiveData<String> = _savedID

    private val _isDone = MutableSharedFlow<Boolean>()
    val isDone : SharedFlow<Boolean>
        get() = _isDone.asSharedFlow()

    fun tryLogin(isRemember: Boolean, id: String, password: String) {
        viewModelScope.launch {
            if (isRemember) {
                appSettingRepository.saveId(id)
            } else {
                appSettingRepository.saveId("")
            }
            val userDocumentID = userRepository.login(id = id, pw = password)
            _login.value = userDocumentID
        }
    }

    fun saveLoggedInfo(documentId: String) = viewModelScope.launch {
        appSettingRepository.updateUserDocumentId(documentId)

        _isDone.emit(true)
    }

    fun loadSavedID() = viewModelScope.launch {
        appSettingRepository.getId().collect {
            _savedID.value = it
        }
    }
}