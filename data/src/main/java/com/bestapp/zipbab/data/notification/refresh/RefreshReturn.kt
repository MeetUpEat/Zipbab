package com.bestapp.zipbab.data.notification.refresh

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshReturn(
    @Json(name = "access_token")val accessToken: String,
    @Json(name = "expires_in")val expiresIn: Int,
    @Json(name = "client_id")val idToken: String,
    @Json(name = "refresh_token")val refreshToken: String,
    @Json(name = "scope")val scope : String,
    @Json(name = "token_type")val tokenType: String,
)
