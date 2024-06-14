package com.bestapp.zipbab.ui.home.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.zipbab.model.FilterUiState

class CostDiffUtil : DiffUtil.ItemCallback<FilterUiState.CostUiState>() {

    override fun areItemsTheSame(
        oldItem: FilterUiState.CostUiState,
        newItem: FilterUiState.CostUiState
    ): Boolean =
        oldItem.icon == newItem.icon

    override fun areContentsTheSame(
        oldItem: FilterUiState.CostUiState,
        newItem: FilterUiState.CostUiState
    ): Boolean =
        oldItem == newItem
}