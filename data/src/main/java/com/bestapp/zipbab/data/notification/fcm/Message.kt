package com.bestapp.zipbab.data.notification.fcm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "token")val token: String,
    @Json(name = "notification")val notification: NotificationData
)

