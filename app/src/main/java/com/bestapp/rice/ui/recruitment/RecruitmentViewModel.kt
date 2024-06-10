package com.bestapp.rice.ui.recruitment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.User
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecruitmentViewModel @Inject constructor (
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository
): ViewModel() {
    private val _recruit = MutableLiveData<Boolean>()
    val recruit : LiveData<Boolean> = _recruit

    fun registerMeeting(meeting: Meeting) = viewModelScope.launch {
        meetingRepository.createMeeting(meeting)
    }

    private val _hostInfo = MutableLiveData<User>()
    val hostInfo : LiveData<User> = _hostInfo

    fun getHostInfo(userDocumentId: String) = viewModelScope.launch {
        val result = userRepository.getUser(userDocumentId)
        _hostInfo.value = result
    }

    private val _getDocumentId = MutableLiveData<String>()
    val getDocumentId : LiveData<String> = _getDocumentId

    fun getDocumentId() = viewModelScope.launch {
        appSettingRepository.userPreferencesFlow.collect {
            getHostInfo(it.ifEmpty { "" })
            /*if(it.isEmpty()) {
                //_getDocumentId.value  = ""
                getHostInfo("")
            } else {
                //_getDocumentId.value = it
                getHostInfo(it)
            }*/
        }
    }
}