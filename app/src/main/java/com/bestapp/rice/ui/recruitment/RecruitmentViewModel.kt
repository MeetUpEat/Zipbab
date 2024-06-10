package com.bestapp.rice.ui.recruitment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.model.remote.Meeting
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

    var priKey = UUID.randomUUID() //임시 추후에 LoginFragment에 옮길예정
    private val _hostInfo = MutableLiveData<String>()
    val hostInfo : LiveData<String> = _hostInfo

    fun getHostInfo() = viewModelScope.launch {
        appSettingRepository.saveDocument(priKey.toString())
    }
}