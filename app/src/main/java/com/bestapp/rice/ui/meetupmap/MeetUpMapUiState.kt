package com.bestapp.rice.ui.meetupmap

import com.bestapp.rice.model.UserUiState

data class MeetUpMapUiState(
    val meetUpMapUserUis: List<MeetUpMapUserUi> = emptyList()
)

fun UserUiState.toMeetUpMapUi() = MeetUpMapUserUi(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
    placeLocationUiState = placeLocationUiState
)
