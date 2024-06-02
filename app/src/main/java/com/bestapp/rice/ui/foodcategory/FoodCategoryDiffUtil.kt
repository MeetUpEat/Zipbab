package com.bestapp.rice.ui.foodcategory

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.rice.model.MeetingUiState

class FoodCategoryDiffUtil : DiffUtil.ItemCallback<MeetingUiState>() {

    override fun areItemsTheSame(oldItem: MeetingUiState, newItem: MeetingUiState): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: MeetingUiState, newItem: MeetingUiState): Boolean =
        oldItem == newItem
}
