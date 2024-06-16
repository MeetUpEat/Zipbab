package com.bestapp.zipbab.ui.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemCostBinding
import com.bestapp.zipbab.model.FilterUiState

class CostViewHolder(
    parent: ViewGroup,
    private val onCostClick: (costUiState: FilterUiState.CostUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_cost, parent, false)
) {

    private val binding = ItemCostBinding.bind(itemView)

    fun bind(
        costUiState: FilterUiState.CostUiState
    ) {

        binding.iv.load(costUiState.icon)
        binding.tv.text = costUiState.name
        binding.root.setOnClickListener {
            onCostClick(costUiState)
        }
    }
}