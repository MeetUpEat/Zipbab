package com.bestapp.rice.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @param is_end 현재 페이지가 마지막 페이지인지 여부
 * @param pageable_count total_count 중 노출 가능 문서 수 (최대 45)
 * @param total_count 	검색어에 검색된 문서 수
 */
@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "is_end") val is_end: Boolean,
    @Json(name = "pageable_count") val pageable_count: Int,
    @Json(name = "total_count") val total_count: Int
)