package com.bestapp.rice.data.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMsg(
    @Json(name ="userId") val userId: String,
    @Json(name ="pushToken") val pushToken: String,
    @Json(name ="date") val date: Long,
    @Json(name ="newPushToken") val newPushToken: String
)