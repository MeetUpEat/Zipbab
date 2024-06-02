package com.bestapp.rice.data.notification

import retrofit2.http.Field


data class PushMsgJson(
    val collapse: String,
    val time_to_live: Int,
    val priority: String,
    val notification: NotificationKey
)