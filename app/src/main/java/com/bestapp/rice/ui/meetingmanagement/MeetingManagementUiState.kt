package com.bestapp.rice.ui.meetingmanagement

import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.PlaceLocation

data class MeetingManagementUiState(
    val meetingDocumentID: String,
    val title: String,
    val titleImage: String,
    val placeLocation: PlaceLocation,
    val activation: Boolean,
    val isDoneReview: Boolean,
    val time: String,  // 애매함
    val host: String,  // 애매함
)

fun Meeting.createFrom(isDoneReview: Boolean) = MeetingManagementUiState(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocation = placeLocation,
    activation = activation,
    time = time,
    host = host,
    isDoneReview = isDoneReview,
)

