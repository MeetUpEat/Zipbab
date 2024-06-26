package com.bestapp.zipbab.ui.cost.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.bestapp.zipbab.model.FilterUiState

class TabItemDiffUtil : DiffUtil.ItemCallback<FilterUiState.CostUiState>() {

    override fun areItemsTheSame(oldItem: FilterUiState.CostUiState, newItem: FilterUiState.CostUiState): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FilterUiState.CostUiState, newItem: FilterUiState.CostUiState): Boolean =
        oldItem == newItem
}
