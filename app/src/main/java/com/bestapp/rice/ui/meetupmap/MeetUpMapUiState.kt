package com.bestapp.rice.ui.meetupmap

import com.bestapp.rice.model.args.MeetingUi

data class MeetUpMapUiState(
    val meetUpMapMeetingUis: List<MeetingUi> = emptyList(),
)
