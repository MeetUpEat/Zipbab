package com.bestapp.rice.ui.foodcategory

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.rice.model.MeetingUiState

class FoodCategoryAdapter(
    private val onFoodCategoryClick: (meetingUiState: MeetingUiState) -> Unit,
) : ListAdapter<MeetingUiState, FoodCategoryViewHolder>(FoodCategoryDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodCategoryViewHolder =
        FoodCategoryViewHolder(parent = parent, onFoodCategoryClick = onFoodCategoryClick)

    override fun onBindViewHolder(holder: FoodCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}