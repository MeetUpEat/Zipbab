package com.bestapp.rice.data.model.remote.kakaomap

/**
 * @param documents 응답 결과
 * @param meta 응답 관련 정보
 */
data class SearchLocation(
    val documents: List<Document>,
    val meta: Meta
)