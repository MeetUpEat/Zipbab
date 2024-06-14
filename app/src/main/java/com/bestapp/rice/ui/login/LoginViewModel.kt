package com.bestapp.rice.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
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
    private val appSettingRepository: AppSettingRepository,
    private val meetingRepository: MeetingRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _login = MutableLiveData<String>()
    val login : LiveData<String> = _login

    private val _savedID = MutableLiveData<String>()
    val savedID : LiveData<String> = _savedID

    private val _isDone = MutableSharedFlow<MoveNavigation>()
    val isDone : SharedFlow<MoveNavigation>
        get() = _isDone.asSharedFlow()

    private var meetingDocumentID = ""
    private var hostDocumentID = ""

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("meetingDocumentID")?.let {
                if(it.isNotEmpty()) {
                    meetingDocumentID = it
                    runCatching {
                        meetingRepository.getMeeting(it)
                    }.onSuccess { result ->
                        Log.e("host", "${result}")
                        hostDocumentID = result.hostUserDocumentID
                    }
                }
            }
        }
    }

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

    fun updateDocumentId(documentId: String) = viewModelScope.launch {
        appSettingRepository.updateUserDocumentId(documentId)
        Log.e("host", documentId)
        Log.e("host", hostDocumentID)
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


    fun loadSavedID() = viewModelScope.launch {
        appSettingRepository.getId().collect {
            _savedID.value = it
        }
    }

    fun getMeetingDocumentID() : String{
        return meetingDocumentID
    }
}