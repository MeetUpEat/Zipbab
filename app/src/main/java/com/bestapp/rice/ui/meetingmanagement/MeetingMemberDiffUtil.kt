package com.bestapp.rice.ui.meetingmanagement

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.rice.model.UserUiState

class MeetingMemberDiffUtil : DiffUtil.ItemCallback<UserUiState>() {

    override fun areItemsTheSame(
        oldItem: UserUiState,
        newItem: UserUiState
    ): Boolean =
        oldItem.userDocumentID == newItem.userDocumentID

    override fun areContentsTheSame(
        oldItem: UserUiState,
        newItem: UserUiState
    ): Boolean =
        oldItem == newItem
}