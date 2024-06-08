package com.bestapp.rice.model.args

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Meeting
import com.bestapp.rice.data.model.remote.PlaceLocation
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingArg(
    val meetingDocumentID: String,
    val title: String,
    val titleImage: String,
    val placeLocationArg: PlaceLocationArg,
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

fun Meeting.toUi() = MeetingArg(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationArg = placeLocation.toUi(),
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
    activation = activation

)

fun PlaceLocation.toUi() = PlaceLocationArg(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)