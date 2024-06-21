package com.bestapp.zipbab.ui.cost.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemCostCategoryTabItemBinding
import com.bestapp.zipbab.model.FilterUiState

class TabItemViewHolder(
    parent: ViewGroup,
    private val onCostTabItemClick: (foodUiState: FilterUiState.CostUiState, position: Int) -> Unit,
    private val adapter: TabItemAdapter
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_cost_category_tab_item, parent, false)
) {

    private val binding = ItemCostCategoryTabItemBinding.bind(itemView)

    fun bind(
        costUiState: FilterUiState.CostUiState
    ) {
        binding.tv.text = costUiState.name
        binding.root.isSelected = adapterPosition == adapter.getSelectIndex()
        binding.root.setOnClickListener {
            // 선택된 Item과 다른 Item 클릭 시 호출
            if (adapter.getSelectIndex() != adapterPosition) {
                // 새롭게 선택된 View의 isSelected 속성 true
                it.isSelected = true

                // 전에 선택됐던 View의 isSelected 속성 false
                adapter.notifyItemChanged(adapter.getSelectIndex()) // -> onBind 재호출

                // 새롭게 선택된 position값 저장
                adapter.setSelectIndex(adapterPosition)
            }

            onCostTabItemClick(costUiState, adapterPosition)
        }
    }
}
