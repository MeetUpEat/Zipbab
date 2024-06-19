package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @property documentResponses 응답 결과
 * @property metaResponse 응답 관련 정보
 */

@JsonClass(generateAdapter = true)
data class SearchLocationResponse(
    @field:Json(name = "documents") val documentResponses: List<DocumentResponse>,
    @field:Json(name = "meta") val metaResponse: MetaResponse
)