package com.bestapp.rice.ui.meetupmap

import com.bestapp.rice.model.PlaceLocationUiState

data class MeetUpMapUserUi(
    val userDocumentID: String = "",
    val nickname: String = "",
    val profileImage: String = "",
    val placeLocationUiState: PlaceLocationUiState = PlaceLocationUiState()
)