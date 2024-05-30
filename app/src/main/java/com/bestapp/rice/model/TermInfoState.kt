package com.bestapp.rice.model

import com.bestapp.rice.data.model.remote.TermInfoResponse
import java.time.LocalDate

data class TermInfoState(
    val id: Int,
    val content: String,
    val date: LocalDate,
) {
    companion object {
        fun createFrom(termInfoResponse: TermInfoResponse) = TermInfoState(
            id = termInfoResponse.id,
            content = termInfoResponse.content,
            date = termInfoResponse.date,
        )
    }
}