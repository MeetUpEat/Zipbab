package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @param documents 응답 결과
 * @param meta 응답 관련 정보
 */

@JsonClass(generateAdapter = true)
data class SearchLocation(
    @field:Json(name = "documents") val documents: List<Document>,
    @field:Json(name = "meta") val meta: Meta
)