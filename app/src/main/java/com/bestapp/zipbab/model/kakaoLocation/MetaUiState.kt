package com.bestapp.zipbab.model.kakaoLocation

import com.bestapp.zipbab.data.model.remote.kakaomap.MetaResponse

data class MetaUiState(
    val isEnd: Boolean = false,
    val pageableCount: Int = Int.MIN_VALUE,
    val totalCount: Int = Int.MIN_VALUE,
)

fun MetaResponse.toUiState() = MetaUiState(
    isEnd = isEnd ?: false,
    pageableCount = pageableCount ?: Int.MIN_VALUE,
    totalCount = totalCount ?: Int.MIN_VALUE
)