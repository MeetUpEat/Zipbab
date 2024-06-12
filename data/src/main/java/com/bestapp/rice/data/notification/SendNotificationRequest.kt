package com.bestapp.rice.data.notification

data class SendNotificationRequest(
    val uuids: List<String>,
    val pushMessage: PushMsgJson,
    val bypass: Boolean
)
