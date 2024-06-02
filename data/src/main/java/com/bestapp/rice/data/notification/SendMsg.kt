package com.bestapp.rice.data.notification

data class SendMsg(
    val userId: String,
    val pushToken: String,
    val date: Long,
    val newPushToken: String
)