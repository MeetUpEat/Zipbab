package com.bestapp.zipbab.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val meetingRepository: MeetingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _login = MutableLiveData<String>()
    val login: LiveData<String> = _login

    private val _savedID = MutableLiveData<String>()
    val savedID: LiveData<String> = _savedID

    private val _isDone = MutableSharedFlow<MoveNavigation>()
    val isDone: SharedFlow<MoveNavigation>
        get() = _isDone.asSharedFlow()

    private var meetingDocumentID = ""
    private var hostDocumentID = ""

    init {
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

    /*private val _login = MutableLiveData<Pair<String, Boolean>>()
    val login: LiveData<Pair<String, Boolean>> = _login*/

    fun loginCompare(id: String, password: String) = viewModelScope.launch {
        val result = userRepository.login(id = id, pw = password)
        _login.value = result
    }

    /*private val _isDone = MutableSharedFlow<MoveNavigation>()

    val isDone: SharedFlow<MoveNavigation>
        get() = _isDone.asSharedFlow()*/

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

        _isDone.emit(
            if (meetingDocumentID.isEmpty()) {
                MoveNavigation.GOBACK
            } else if (hostDocumentID == documentId) {
                MoveNavigation.GOMEETINGMANGERAGEMENT
            } else {
                MoveNavigation.GOBACK
            }
        )
    }

    private val _loginLoad = MutableLiveData<String>()
    val loginLoad: LiveData<String> = _loginLoad

    fun loginLoad() = viewModelScope.launch {
        appSettingRepository.getId().collect {
            if (it.isEmpty()) {
                _loginLoad.value = ""
            } else {
                _loginLoad.value = it
            }

            fun loadSavedID() = viewModelScope.launch {
                appSettingRepository.getId().collect {
                    _savedID.value = it
                }
            }

            fun getMeetingDocumentID(): String {
                return meetingDocumentID
            }
        }
    }

    fun loadSavedID() = viewModelScope.launch {
        appSettingRepository.getId().collect {
            _savedID.value = it
        }
    }

    fun getMeetingDocumentId() : String{
        return meetingDocumentID
    }
}