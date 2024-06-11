package com.bestapp.rice.ui.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemCostBinding
import com.bestapp.rice.model.FilterUiState

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