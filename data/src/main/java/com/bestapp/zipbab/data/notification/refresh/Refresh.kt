package com.bestapp.zipbab.data.notification.refresh

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Refresh (
    @Json(name = "client_id")val clientId: String,
    @Json(name = "client_secret")val clientSecret: String,
    @Json(name = "code")val code: String,
    @Json(name = "grant_type")val grantType: String,
    @Json(name = "redirect_uri")val redirectUri: String
)