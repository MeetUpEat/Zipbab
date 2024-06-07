package com.bestapp.rice.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.network.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor (

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