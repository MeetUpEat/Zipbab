package com.bestapp.zipbab.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
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
    private val _getUserData = MutableLiveData<UserResponse>()
    val getUserData : LiveData<UserResponse> = _getUserData

    fun getUserData() = viewModelScope.launch {
        appSettingRepository.userDocumentID.collect {
            val result = userRepository.getUser(it)
            _getUserData.value = result
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

//    fun addNotifyList(udi: String, notifyType: NotificationType.UserNotification) = viewModelScope.launch { //데이터 추가용 함수
//        val result = userRepository.getUser(udi)
//        val list = result.notificationList + notifyType
//        userRepository.addNotifyListInfo(udi, FieldValue.arrayUnion(list as ArrayList<NotificationType.UserNotification>))
//    }

    fun removeNotifyList(position: Int) = viewModelScope.launch {
        appSettingRepository.userDocumentID.collect {
            val result = userRepository.getUser(it).notifications as ArrayList<NotificationTypeResponse>
            result.removeAt(position)
            userRepository.removeItem(it, result, position)
        }
    }

    fun transUserMeeting(mdi: String, udi: String) = viewModelScope.launch {
        meetingRepository.approveMember(mdi, udi)
    }
}