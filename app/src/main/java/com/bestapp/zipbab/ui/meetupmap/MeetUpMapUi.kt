package com.bestapp.zipbab.ui.meetupmap

import com.bestapp.zipbab.data.model.remote.Meeting
import com.bestapp.zipbab.model.args.PlaceLocationUi
import com.bestapp.zipbab.model.args.toUi

data class MeetUpMapUi(
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
    val distance: Double,
    val distanceByUser: String,
    val isHost: Boolean,
) {
    val shortTitle get() = if (title.length > 15) {
        String.format("%s...", title.substring(0, 14))
    } else {
        title
    }
}

fun Meeting.toUi(distance: Double, isHost: Boolean) = MeetUpMapUi(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUi = placeLocation.toUi(),
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
    distance = distance,
    distanceByUser = "",
    isHost = isHost
)