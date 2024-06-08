package com.bestapp.rice.model

data class MeetingUiState(
    val meetingDocumentID: String = "",
    val title: String = "",
    val titleImage: String = "",
    val placeLocationUiState: PlaceLocationUiState = PlaceLocationUiState(),
    val time: String = "",
    val recruits: Int = Int.MIN_VALUE,
    val description: String = "",
    val mainMenu: String = "",
    val costValueByPerson: Int = Int.MIN_VALUE,
    val costTypeByPerson: Int = Int.MIN_VALUE,
    val host: String = "",
    val members: List<String> = listOf(),
    val pendingMembers: List<String> = listOf(),
    val attendanceCheck: List<String> = listOf(),
    val activation: Boolean = true,
)