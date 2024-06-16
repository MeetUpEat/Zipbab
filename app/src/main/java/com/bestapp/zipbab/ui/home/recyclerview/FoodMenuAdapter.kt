package com.bestapp.zipbab.ui.home.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.FilterUiState

class FoodMenuAdapter(
    private val onFoodMenuClick: (foodUiState: FilterUiState.FoodUiState) -> Unit,
) : ListAdapter<FilterUiState.FoodUiState, FoodMenuViewHolder>(FoodMenuDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodMenuViewHolder = FoodMenuViewHolder(parent = parent, onFoodMenuClick = onFoodMenuClick)

    override fun onBindViewHolder(holder: FoodMenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}