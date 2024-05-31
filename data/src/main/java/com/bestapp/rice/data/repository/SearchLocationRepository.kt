package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.kakaomap.SearchLocation

interface SearchLocationRepository {
    suspend fun convertLocation(
        query: String,
        analyze_type: String = "similar",
        page: Int = 1,
        size: Int = 10,
    ): SearchLocation
}