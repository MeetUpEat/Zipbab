package com.bestapp.rice.data.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DownloadToken(
    @Json(name ="uuid") val uuid: String,
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "push_type") val pushType: String,
    @Json(name = "push_token") val pushToken: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)