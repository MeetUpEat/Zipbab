package com.bestapp.rice.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.notification.DownloadToken
import com.bestapp.rice.data.notification.PushMsgJson
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor (
    private val notificationRepository: NotificationRepository,
    private val appSettingRepository: AppSettingRepository
) : ViewModel() {
    private val _userInfo = MutableLiveData<String>()
    val userInfo: LiveData<String> = _userInfo

    fun getUserUUID() = viewModelScope.launch{
        appSettingRepository.userPreferencesFlow.collect {
            _userInfo.value = it
        }
    }

    fun registerTokenKaKao(uuid: String, deviceId: String, pushToken: String) = viewModelScope.launch {
        notificationRepository.registerToken(uuid, deviceId, "fcm", pushToken)
    }

    private val _downloadInfo = MutableLiveData<DownloadToken>()
    val downloadInfo : LiveData<DownloadToken> = _downloadInfo

    fun downloadKaKao(uuid: String) = viewModelScope.launch {
        val result = notificationRepository.downloadToken(uuid)
        _downloadInfo.value = result
    }

    fun deleteTokenKaKao(uuid: String, deviceId: String, pushToken: String) = viewModelScope.launch {
        notificationRepository.deleteToken(uuid, deviceId, pushToken)
    }

    fun sendMsgKaKao(uuid: List<String>, pushMsgJson: PushMsgJson) = viewModelScope.launch {
        notificationRepository.sendNotification(uuid, pushMsgJson, false)
    }
}