package com.bestapp.zipbab.ui.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemFoodMenuBinding
import com.bestapp.zipbab.model.FilterUiState

class FoodMenuViewHolder(
    parent: ViewGroup,
    private val onFoodMenuClick: (foodUiState: FilterUiState.FoodUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_food_menu, parent, false)
) {

    private val binding = ItemFoodMenuBinding.bind(itemView)

    fun bind(
        foodUiState: FilterUiState.FoodUiState
    ) {
        binding.iv.load(foodUiState.icon) {
            crossfade(true)
            placeholder(R.drawable.shape_et_round_bg)
            transformations(RoundedCornersTransformation())
        }
        binding.tv.text = foodUiState.name
        binding.root.setOnClickListener {
            onFoodMenuClick(foodUiState)
        }
    }
}
