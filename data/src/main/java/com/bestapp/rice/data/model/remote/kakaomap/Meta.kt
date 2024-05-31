package com.bestapp.rice.data.model.remote.kakaomap

/**
 * @param is_end 현재 페이지가 마지막 페이지인지 여부
 * @param pageable_count total_count 중 노출 가능 문서 수 (최대 45)
 * @param total_count 	검색어에 검색된 문서 수
 */
data class Meta(
    val is_end: Boolean,
    val pageable_count: Int,
    val total_count: Int
)