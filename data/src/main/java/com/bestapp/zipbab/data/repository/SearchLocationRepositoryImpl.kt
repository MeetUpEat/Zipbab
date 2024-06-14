package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.di.NetworkProviderModule
import com.bestapp.zipbab.data.model.remote.kakaomap.SearchLocation
import com.bestapp.zipbab.data.network.SearchLocationService
import javax.inject.Inject

internal class SearchLocationRepositoryImpl @Inject constructor(
    @NetworkProviderModule.KakaoMapRetrofit private val searchLocationService: SearchLocationService
) : SearchLocationRepository {
    override suspend fun convertLocation(
        query: String,
        analyzeType: String,
        page: Int,
        size: Int,
    ): SearchLocation {
        return searchLocationService.convertLocation(
            query,
            analyzeType,
            page,
            size,
        )
    }
}