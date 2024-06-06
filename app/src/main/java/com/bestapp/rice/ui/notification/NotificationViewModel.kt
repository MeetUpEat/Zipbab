package com.bestapp.rice.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.UUID

class NotificationViewModel(

) : ViewModel() {
    private var uuid : UUID = UUID.randomUUID()

    fun RegisterTokenKaKao(deviceId: String, pushToken: String) = viewModelScope.launch {
        RetrofitClient.notifyService.registerToken(
            uuid.toString(),
            "",
            "fcm",
            ""
        )
    }

    fun DownloadKaKao(uuid: String) = viewModelScope.launch {

    }

    fun DeleteTokenKaKao(uuid: String, deviceId: String, pushToken: String) = viewModelScope.launch {

    }

    fun SendMsgKaKao(uuid: String) = viewModelScope.launch {

    }
}