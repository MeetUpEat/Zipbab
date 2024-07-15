package com.bestapp.zipbab.ui.mettinginfo

sealed interface MeetingInfoEvent {
    data object Default : MeetingInfoEvent
    data object RegisterInProgress : MeetingInfoEvent
    data object RegisterFail : MeetingInfoEvent
    data object RegisterSuccess : MeetingInfoEvent
    data object EndMeetingSuccess : MeetingInfoEvent
    data object EndMeetingFail : MeetingInfoEvent
}