package com.bestapp.zipbab.data.notification.access

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Access(
    @Json(name = "client_id")val clientId: String,
    @Json(name = "client_secret")val clientSecret: String,
    @Json(name = "refresh_token")val refreshToken: String,
    @Json(name = "grant_type")val grantType: String,
)
