package com.bestapp.rice.data.model.remote

/**
 * @param id 평가 대상 아이디
 * @param votingPoint 평가한 점수
 */

data class Review(
    val id: String,
    val votingPoint: Double,
)