package com.bestapp.zipbab.ui.foodcategory.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.zipbab.model.MeetingUiState

class FoodCategoryDiffUtil : DiffUtil.ItemCallback<MeetingUiState>() {

    override fun areItemsTheSame(oldItem: MeetingUiState, newItem: MeetingUiState): Boolean =
        oldItem.meetingDocumentID == newItem.meetingDocumentID

    override fun areContentsTheSame(oldItem: MeetingUiState, newItem: MeetingUiState): Boolean =
        oldItem == newItem
}
