package com.bestapp.zipbab.data.model.remote

/**
 * @property id 평가 대상 아이디
 * @property votingPoint 평가한 점수
 */

data class Review(
    val id: String,
    val votingPoint: Double,
)