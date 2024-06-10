package com.bestapp.rice.ui.meetupmap

import com.bestapp.rice.model.args.MeetingArg

data class MeetUpMapUiState(
    val meetUpMapMeetingUis: List<MeetingArg> = emptyList(),
)
