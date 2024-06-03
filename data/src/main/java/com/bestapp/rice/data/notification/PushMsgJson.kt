package com.bestapp.rice.data.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Field


@JsonClass(generateAdapter = true)
data class PushMsgJson(
    @Json(name ="collapse")val collapse: String,
    @Json(name ="time_to_live")val timeToLive: Int,
    @Json(name ="priority")val priority: String,
    @field:Json(name = "notification")val notification: NotificationKey
)