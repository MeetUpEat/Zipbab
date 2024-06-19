package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @param isEnd 현재 페이지가 마지막 페이지인지 여부
 * @param pageableCount total_count 중 노출 가능 문서 수 (최대 45)
 * @param totalCount    검색어에 검색된 문서 수
 */
@JsonClass(generateAdapter = true)
data class MetaResponse(
    @Json(name = "is_end") val isEnd: Boolean,
    @Json(name = "pageable_count") val pageableCount: Int,
    @Json(name = "total_count") val totalCount: Int
)