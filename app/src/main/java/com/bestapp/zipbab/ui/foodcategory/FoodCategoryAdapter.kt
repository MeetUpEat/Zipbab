package com.bestapp.zipbab.ui.foodcategory

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.MeetingUiState

class FoodCategoryAdapter(
    private val onFoodCategoryClick: (meetingUiState: MeetingUiState) -> Unit,
) : ListAdapter<MeetingUiState, FoodCategoryViewHolder>(FoodCategoryDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodCategoryViewHolder =
        FoodCategoryViewHolder(parent, onFoodCategoryClick)

    override fun onBindViewHolder(holder: FoodCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}