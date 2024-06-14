package com.bestapp.rice.data.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForFcm(
    @Json(name ="collapse")val collapse: String,
    @Json(name ="time_to_live")val timeToLive: Int,
    @Json(name ="priority")val priority: String,
    @Json(name = "notification")val notification: NotificationKey
)
