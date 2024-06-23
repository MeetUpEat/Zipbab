package com.bestapp.zipbab.data.notification.access

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Access(
    @Json(name = "client_id")val clientId: String,
    @Json(name = "client_secret")val clientSecret: String,
    @Json(name = "code")val code: String,
    @Json(name = "redirect_uri")val redirectUri: String,
    @Json(name = "grant_type")val grantType: String,
)
