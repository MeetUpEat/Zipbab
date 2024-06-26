package com.bestapp.zipbab.ui.foodcategory.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.zipbab.model.FilterUiState

class TabItemDiffUtil : DiffUtil.ItemCallback<FilterUiState.FoodUiState>() {

    override fun areItemsTheSame(oldItem: FilterUiState.FoodUiState, newItem: FilterUiState.FoodUiState): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FilterUiState.FoodUiState, newItem: FilterUiState.FoodUiState): Boolean =
        oldItem == newItem
}
