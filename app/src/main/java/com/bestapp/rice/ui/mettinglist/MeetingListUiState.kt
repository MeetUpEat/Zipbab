package com.bestapp.rice.ui.mettinglist

import com.bestapp.rice.model.args.MeetingUi

// Meeting(data) -> MeetingUi(ui) -> MeetingListUi(ui)
data class MeetingListUiState(
    val meetingUis: List<MeetingListUi> = emptyList(),
)

/**
 *  Fragment 간의 데이터 전달을 위함
 */
fun MeetingListUi.toMeetingUi() = MeetingUi(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUi = placeLocationUi,
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
    activation = activation
)
