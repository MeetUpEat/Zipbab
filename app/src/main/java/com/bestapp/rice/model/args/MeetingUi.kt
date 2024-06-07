package com.bestapp.rice.model.args

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Meeting
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingUi(
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
) : Parcelable

fun Meeting.createFromMeeting() = MeetingUi(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUi = PlaceLocationUi(
        locationAddress = placeLocation.locationAddress,
        locationLat = placeLocation.locationLat,
        locationLong = placeLocation.locationLong,
    ),
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
)