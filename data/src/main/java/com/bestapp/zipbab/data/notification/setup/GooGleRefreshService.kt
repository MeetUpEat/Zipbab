package com.bestapp.zipbab.data.notification.setup

import com.bestapp.zipbab.data.notification.refresh.Refresh
import com.bestapp.zipbab.data.notification.refresh.RefreshReturn
import retrofit2.http.Body
import retrofit2.http.POST

interface GooGleRefreshService {
    @POST("token")
    fun getRefreshTokenInformation(
        @Body refresh: Refresh
    ) : RefreshReturn
}