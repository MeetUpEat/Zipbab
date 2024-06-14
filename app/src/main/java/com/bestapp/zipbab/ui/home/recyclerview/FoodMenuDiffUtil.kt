package com.bestapp.zipbab.ui.home.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.zipbab.model.FilterUiState

class FoodMenuDiffUtil : DiffUtil.ItemCallback<FilterUiState.FoodUiState>() {

    override fun areItemsTheSame(
        oldItem: FilterUiState.FoodUiState,
        newItem: FilterUiState.FoodUiState
    ): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(
        oldItem: FilterUiState.FoodUiState,
        newItem: FilterUiState.FoodUiState
    ): Boolean =
        oldItem == newItem
}
