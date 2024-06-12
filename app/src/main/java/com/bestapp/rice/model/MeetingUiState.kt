package com.bestapp.rice.model

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
    val hostTemperature: Double,
    val members: List<String>,
    val pendingMembers: List<String>,
    val attendanceCheck: List<String>,
    val activation: Boolean,
)