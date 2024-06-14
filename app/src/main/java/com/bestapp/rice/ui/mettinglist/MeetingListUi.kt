package com.bestapp.rice.ui.mettinglist

import com.bestapp.rice.data.model.remote.MeetingResponse
import com.bestapp.rice.data.model.remote.PlaceLocation
import com.bestapp.rice.model.args.PlaceLocationUi

data class MeetingListUi(
    val meetingDocumentID: String,
    val title: String,
    val titleImage: String,
    val placeLocationUi: PlaceLocationUi,
    val time: String,
    val recruits: Int,
    val description: String,
    val mainMenu: String,
    val costValueByPerson: Int,
    val costTypeByPerson: Int,
    val hostUserDocumentID: String,
    val hostTemperature: Double,
    val members: List<String>,
    val pendingMembers: List<String>,
    val attendanceCheck: List<String>,
    val activation: Boolean,
    // TODO: User data class 수정된 뒤, 값 셋업
    val isDoneReview: Boolean = false,
    val isHost: Boolean = false
)

fun MeetingResponse.toMeetingListUi(
    isDoneReview: Boolean,
    isHost: Boolean
) = MeetingListUi(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUi = placeLocation.toPlaceLocationUi(),
    time = time,
    recruits = recruits,
    description = description,
    mainMenu = mainMenu,
    costValueByPerson = costValueByPerson,
    costTypeByPerson = costTypeByPerson,
    hostUserDocumentID = hostUserDocumentID,
    hostTemperature = hostTemperature,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation,
    isDoneReview = isDoneReview,
    isHost = isHost
)

fun PlaceLocation.toPlaceLocationUi() = PlaceLocationUi(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)