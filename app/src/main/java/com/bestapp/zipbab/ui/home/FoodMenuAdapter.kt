package com.bestapp.zipbab.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemFoodMenuBinding
import com.bestapp.zipbab.model.FilterUiState

class FoodMenuAdapter(
    private val onFoodMenuClick: (foodUiState: FilterUiState.FoodUiState) -> Unit,
) : ListAdapter<FilterUiState.FoodUiState, FoodMenuAdapter.FoodMenuViewHolder>(diff) {

    class FoodMenuViewHolder(
        private val binding: ItemFoodMenuBinding,
        private val onFoodMenuClick: (foodUiState: FilterUiState.FoodUiState) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(foodUiState: FilterUiState.FoodUiState) {
            binding.root.setOnClickListener {
                onFoodMenuClick(foodUiState)
            }

            binding.ivIcon.load(foodUiState.icon) {
                crossfade(true)
                placeholder(R.drawable.shape_et_round_bg)
                transformations(RoundedCornersTransformation())
            }
            binding.tvName.text = foodUiState.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodMenuViewHolder {
        return FoodMenuViewHolder(
            ItemFoodMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onFoodMenuClick,
        )
    }

    override fun onBindViewHolder(holder: FoodMenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<FilterUiState.FoodUiState>() {
            override fun areItemsTheSame(
                oldItem: FilterUiState.FoodUiState,
                newItem: FilterUiState.FoodUiState
            ): Boolean = oldItem === newItem

            override fun areContentsTheSame(
                oldItem: FilterUiState.FoodUiState,
                newItem: FilterUiState.FoodUiState
            ): Boolean = oldItem == newItem
        }
    }
}