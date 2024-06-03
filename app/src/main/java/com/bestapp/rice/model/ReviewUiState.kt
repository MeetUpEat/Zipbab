package com.bestapp.rice.model

import com.bestapp.rice.data.model.remote.Review
import kotlin.String

data class ReviewUiState(
    val id: String,
    val votingPoint: Double,
) {
    companion object {
        fun createFrom(review: Review) = ReviewUiState(
            id = review.id,
            votingPoint = review.votingPoint,
        )
    }
}
