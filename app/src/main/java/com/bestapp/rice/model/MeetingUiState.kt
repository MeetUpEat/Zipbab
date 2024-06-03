package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Meeting
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingUiState(
    val meetingDocumentID: kotlin.String,
    val title: kotlin.String,
    val titleImage: String,
    val placeLocationUiState: PlaceLocationUiState,
    val time: kotlin.String,
    val recruits: Int,
    val description: kotlin.String,
    val mainMenu: kotlin.String,
    val costValueByPerson: Int,
    val costTypeByPerson: Int,
    val host: kotlin.String,
    val members: List<kotlin.String>,
    val pendingMembers: List<kotlin.String>,
    val attendanceCheck: List<kotlin.String>,
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