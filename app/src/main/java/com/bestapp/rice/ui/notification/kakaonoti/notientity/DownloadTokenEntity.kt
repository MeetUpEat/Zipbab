package com.bestapp.rice.ui.notification.kakaonoti.notientity

import com.google.type.DateTime

data class DownloadTokenEntity(
    val uuid: String,
    val device_id: String,
    val push_type: String,
    val push_token: String,
    val created_at: DateTime,
    val updated_at: DateTime
)