package com.bestapp.zipbab.data.notification.fcm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationData(
    @Json(name = "title")val title: String,
    @Json(name = "body")val body: String
)
