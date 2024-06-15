package com.bestapp.zipbab.data.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendNotificationRequest(
    @Json(name = "uuids")val uuids: List<String>,
    @Json(name = "push_message")val pushMessage: PushMsgJson,
    @Json(name = "by_pass")val bypass: Boolean
)
