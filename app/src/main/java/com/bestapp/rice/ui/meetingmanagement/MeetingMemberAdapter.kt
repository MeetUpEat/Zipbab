package com.bestapp.rice.ui.meetingmanagement

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.UserUiState

class MeetingMemberAdapter(
    private val onCostClick: (userUiState:UserUiState) -> Unit,
) : ListAdapter<UserUiState, MeetingMemberViewHolder>(MeetingMemberDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MeetingMemberViewHolder = MeetingMemberViewHolder(parent = parent, onCostClick = onCostClick)

    override fun onBindViewHolder(holder: MeetingMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}