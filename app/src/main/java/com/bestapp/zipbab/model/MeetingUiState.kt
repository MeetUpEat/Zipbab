package com.bestapp.zipbab.model

data class MeetingUiState(
    val meetingDocumentID: String = "",
    val title: String = "",
    val titleImage: String = "",
    val placeLocationUiState: PlaceLocationUiState = PlaceLocationUiState(),
    val time: String = "",
    val recruits: Int = 0,
    val description: String = "",
    val mainMenu: String = "",
    val costValueByPerson: Int = 0,
    val costTypeByPerson: Int = 0,
    val hostUserDocumentID: String = "",
    val hostTemperature: Double = 0.0,
    val members: List<String> = emptyList(),
    val pendingMembers: List<String> = emptyList(),
    val attendanceCheck: List<String> = emptyList(),
    val activation: Boolean = false,
)