package com.bestapp.rice.ui.mettinglist

import com.bestapp.rice.data.model.remote.Meeting
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
    val host: String,
    val members: List<String>,
    val pendingMembers: List<String>,
    val attendanceCheck: List<String>,
    val activation: Boolean,

    val isDoneReview: Boolean = false,
)

fun Meeting.toMeetingListUi(isDoneReview: Boolean) = MeetingListUi(
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
    host = host,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation,
    isDoneReview = isDoneReview
)

fun PlaceLocation.toPlaceLocationUi() = PlaceLocationUi(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)