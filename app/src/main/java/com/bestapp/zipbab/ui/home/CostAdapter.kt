package com.bestapp.zipbab.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.databinding.ItemCostBinding
import com.bestapp.zipbab.model.FilterUiState

class CostAdapter(
    private val onCostClick: (costUiState: FilterUiState.CostUiState) -> Unit,
) : ListAdapter<FilterUiState.CostUiState, CostAdapter.CostViewHolder>(diff) {

    class CostViewHolder(
        private val binding: ItemCostBinding,
        private val onCostClick: (costUiState: FilterUiState.CostUiState) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(costUiState: FilterUiState.CostUiState) {
            binding.root.setOnClickListener {
                onCostClick(costUiState)
            }

            binding.ivIcon.load(costUiState.icon)
            binding.tvName.text = costUiState.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CostViewHolder {
        return CostViewHolder(
            ItemCostBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onCostClick,
        )
    }

    override fun onBindViewHolder(holder: CostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<FilterUiState.CostUiState>() {
            override fun areItemsTheSame(
                oldItem: FilterUiState.CostUiState,
                newItem: FilterUiState.CostUiState
            ): Boolean = oldItem.icon == newItem.icon

            override fun areContentsTheSame(
                oldItem: FilterUiState.CostUiState,
                newItem: FilterUiState.CostUiState
            ): Boolean = oldItem == newItem
        }
    }
}