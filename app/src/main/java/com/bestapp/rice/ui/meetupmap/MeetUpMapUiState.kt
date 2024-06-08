package com.bestapp.rice.ui.meetupmap

import android.graphics.Bitmap
import com.bestapp.rice.model.UserUiState

data class MeetUpMapUiState(
    val meetUpMapUserUis: List<MeetUpMapUserUi> = emptyList(),
    val userProfileImages: List<Bitmap> = emptyList(),
)

fun UserUiState.toMeetUpMapUi() = MeetUpMapUserUi(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
    placeLocationUiState = placeLocationUiState
)
