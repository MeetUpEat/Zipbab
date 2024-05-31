package com.bestapp.rice.data.repository

import androidx.annotation.IntRange
import com.bestapp.rice.data.model.remote.kakaomap.SearchLocation

interface SearchLocationRepository {
    suspend fun convertLocation(
        query: String,
        analyze_type: String = "similar",
        @IntRange(from = 1, to = 45) page: Int = 1,
        @IntRange(from = 1, to = 30) size: Int = 10,
    ): SearchLocation
}