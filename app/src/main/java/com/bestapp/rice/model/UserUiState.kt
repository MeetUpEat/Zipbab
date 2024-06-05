package com.bestapp.rice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUiState(
    val userDocumentID: String,
    val nickname: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val postUiStates: List<PostUiState>,
    val placeLocationUiState: PlaceLocationUiState,
) : Parcelable {
    companion object {

        val Empty = UserUiState(
            userDocumentID = "",
            nickname = "",
            id = "",
            pw = "",
            profileImage = "",
            temperature = 0.0,
            meetingCount = 0,
            postUiStates = listOf(),
            placeLocationUiState = PlaceLocationUiState(
                locationAddress = "",
                locationLat = "",
                locationLong = ""
            )
        )
    }
}
