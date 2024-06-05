package com.bestapp.rice.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.network.RetrofitClient
import kotlinx.coroutines.launch

class NotificationViewModel(

) : ViewModel() {
    private var uuid = 0

    /*fun notifyKaKao() = viewModelScope.launch {
        uuid++
        RetrofitClient.notifyService.registerToken(
            uuid.toString(),
            "",
            "fcm",
            ""
        )
    }*/
}