package com.bestapp.rice.data.notification

data class SendMsgEntity(
    val userId: String,
    val pushToken: String,
    val date: Long,
    val newPushToken: String
)