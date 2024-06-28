package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.di.NetworkProviderModule
import com.bestapp.zipbab.data.notification.access.Access
import com.bestapp.zipbab.data.notification.access.AccessReturn
import com.bestapp.zipbab.data.notification.refresh.Refresh
import com.bestapp.zipbab.data.notification.refresh.RefreshReturn
import com.bestapp.zipbab.data.notification.setup.GooGleRefreshService
import com.bestapp.zipbab.data.notification.setup.GooGleService
import javax.inject.Inject

class ProviderRepositoryImpl @Inject constructor(
    @NetworkProviderModule.GoogleTokenProvider private val gooGleService: GooGleService,
    @NetworkProviderModule.GoogleRefreshTokenProvider private val gooGleRefreshService: GooGleRefreshService
) : ProviderRepository {
    override suspend fun getTokenInfo( //accesstoken만료시 호출
        id: String,
        secret: String,
        code: String,
        uri: String,
        type: String
    ): AccessReturn {
        return gooGleService.getTokenInformation(Access(id, secret, code, uri, type))
    }

    override suspend fun getRefreshInfo( //refresh토큰 만료시 호출
        id: String,
        secret: String,
        code: String,
        type: String,
        uri: String
    ): RefreshReturn {
        return gooGleRefreshService.getRefreshTokenInformation(Refresh(id, secret, code, type, uri))
    }
}