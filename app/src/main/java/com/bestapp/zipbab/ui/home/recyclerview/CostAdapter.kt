package com.bestapp.zipbab.ui.home.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.FilterUiState

class CostAdapter(
    private val onCostClick: (costUiState: FilterUiState.CostUiState) -> Unit,
) : ListAdapter<FilterUiState.CostUiState, CostViewHolder>(CostDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CostViewHolder = CostViewHolder(parent = parent, onCostClick = onCostClick)

    override fun onBindViewHolder(holder: CostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}