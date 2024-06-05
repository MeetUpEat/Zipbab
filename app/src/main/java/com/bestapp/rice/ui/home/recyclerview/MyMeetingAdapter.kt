package com.bestapp.rice.ui.home.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.rice.model.MeetingUiState

class MyMeetingAdapter(
    private val onMyMeetingClick: (meetingUiState: MeetingUiState) -> Unit,
) : ListAdapter<MeetingUiState, MyMeetingViewHolder>(MyMeetingDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyMeetingViewHolder = MyMeetingViewHolder(parent = parent, onMyMeetingClick = onMyMeetingClick)

    override fun onBindViewHolder(holder: MyMeetingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}