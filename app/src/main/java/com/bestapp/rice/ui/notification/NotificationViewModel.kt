package com.bestapp.rice.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.network.RetrofitClient
import kotlinx.coroutines.launch

class NotificationViewModel(

) : ViewModel() {

    fun notifyKaKao() = viewModelScope.launch {
        RetrofitClient.notifyService.registerToken(
            "1234",
            "eJgYAawWR5-tkFLKwqppNx",
            "fcm",
            "APA91bGfyzZW2idOQkvDckS3hmoESUOwggQVtDPkRdW7gZkY_8ISBvxv7zUEyW45U-WPaMTAu60jShNS4d1N0pYE9G3cfVpDZ6GjLx7i1jncK7aC96yODMPmn_hoINMqecq6lF0k2z1g"
        )
    }
}