package com.bestapp.rice.data.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationKey(
    @Json(name ="title")val title: String,
    @Json(name ="body")val body: String,
    @Json(name ="icon")val icon: String,
    @Json(name ="sound")val sound: String,
    @Json(name ="tag")val tag: String,
    @Json(name ="color")val color: String,
    //val click_action: String
)
