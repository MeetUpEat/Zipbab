package com.bestapp.zipbab.ui.mettinglist

import com.bestapp.zipbab.args.MeetingArgs

// Meeting(data) -> MeetingUi(ui) -> MeetingListUi(ui)
data class MeetingListUiState(
    val meetingUis: List<MeetingListUi> = emptyList(),
)

/**
 *  Fragment 간의 데이터 전달을 위함
 */
fun MeetingListUi.toMeetingUi() = MeetingArgs(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationArgs = placeLocationArgs,
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
