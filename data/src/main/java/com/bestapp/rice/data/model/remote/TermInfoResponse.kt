package com.bestapp.rice.data.model.remote

import java.time.LocalDate

/**
 * @param id 약관 구분 아이디
 * @param content 내용
 * @param date 작성 일자
 */
data class TermInfoResponse(
    val id: Int,
    val content: String,
    val date: LocalDate,
)