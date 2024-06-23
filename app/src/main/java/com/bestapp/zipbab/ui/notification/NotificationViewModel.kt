package com.bestapp.zipbab.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.User
import com.bestapp.zipbab.data.notification.fcm.PushNotification
import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.NotificationRepository
import com.bestapp.zipbab.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor (
    private val notificationRepository: NotificationRepository,
    private val appSettingRepository: AppSettingRepository,
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _getUserData = MutableLiveData<User>()
    val getUserData : LiveData<User> = _getUserData

    fun getUserData() = viewModelScope.launch {
        appSettingRepository.userPreferencesFlow.collect {
            val result = userRepository.getUser(it)
            if(result.notificationList.isEmpty()) {
                _getUserData.value = User()
            } else {
                _getUserData.value = result
            }

        }
    }

    fun sendMsgKaKao(message: PushNotification, token: String) = viewModelScope.launch {
        notificationRepository.sendNotification(message, token)
    }

    private val _approveUser = MutableLiveData<Boolean>()
    val approveUser : LiveData<Boolean> = _approveUser
    fun approveMember(meetingDocumentId: String, userDocumentId: String) = viewModelScope.launch {
        val result = meetingRepository.approveMember(meetingDocumentId, userDocumentId)
        _approveUser.value = result
    }

    private val _accessKey = MutableLiveData<String>()
    val accesskey : LiveData<String> = _accessKey

    fun getAccessToken() = viewModelScope.launch {
        val result = userRepository.getAccessToken()
        _accessKey.value = result.access
    }
}