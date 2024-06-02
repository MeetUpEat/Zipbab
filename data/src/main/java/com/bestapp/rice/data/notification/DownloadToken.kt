package com.bestapp.rice.data.notification

data class DownloadToken(
    val uuid: String,
    val device_id: String,
    val push_type: String,
    val push_token: String,
    val created_at: String,
    val updated_at: String
)