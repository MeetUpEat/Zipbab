package com.bestapp.zipbab.data.repository

import androidx.annotation.IntRange
import com.bestapp.zipbab.data.model.remote.kakaomap.SearchLocation

interface SearchLocationRepository {
    suspend fun convertLocation(
        query: String,
        analyzeType: String = "similar",
        @IntRange(from = 1, to = 45) page: Int = 1,
        @IntRange(from = 1, to = 30) size: Int = 10,
    ): SearchLocation
}