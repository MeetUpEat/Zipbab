package com.bestapp.zipbab.data.model.remote

import java.time.LocalDate

/**
 * @property id 약관 구분 아이디
 * @property content 내용
 * @property date 작성 일자
 */
data class TermInfoResponse(
    val id: Int,
    val content: String,
    val date: LocalDate,
)