package com.bestapp.rice.ui.notification.kakaonoti.notientity

data class PushMsgJson(
    val collapse: String,
    val time_to_live: Int,
    val priority: String,
    val notification: NotificationKey
)
