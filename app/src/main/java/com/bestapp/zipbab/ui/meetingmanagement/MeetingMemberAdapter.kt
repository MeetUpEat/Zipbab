package com.bestapp.zipbab.ui.meetingmanagement

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.UserUiState

class MeetingMemberAdapter(
    private val onMemberClick: (userUiState:UserUiState) -> Unit,
) : ListAdapter<UserUiState, MeetingMemberViewHolder>(MeetingMemberDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MeetingMemberViewHolder = MeetingMemberViewHolder(parent, onMemberClick)

    override fun onBindViewHolder(holder: MeetingMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}