package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Meeting
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingUiState(
    val meetingDocumentID: String,
    val title: String,
    val titleImage: String,
    val placeLocationUiState: PlaceLocationUiState,
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
) : Parcelable {

    companion object {
        fun createFrom(meeting: Meeting) = MeetingUiState(
            meetingDocumentID = meeting.meetingDocumentID,
            title = meeting.title,
            titleImage = meeting.titleImage,
            placeLocationUiState = PlaceLocationUiState.createFrom(meeting.placeLocation),
            time = meeting.time,
            recruits = meeting.recruits,
            description = meeting.description,
            mainMenu = meeting.mainMenu,
            costValueByPerson = meeting.costValueByPerson,
            costTypeByPerson = meeting.costTypeByPerson,
            host = meeting.host,
            members = meeting.members,
            pendingMembers = meeting.pendingMembers,
            attendanceCheck = meeting.attendanceCheck,
            activation = meeting.activation,
        )
    }
}