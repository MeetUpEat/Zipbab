package com.bestapp.rice.ui.notification.kakaonoti.notientity

data class SendMsgEntity(
    val userId: String,
    val pushToken: String,
    val date: Long,
    val newPushToken: String
)