package com.bestapp.zipbab.ui.foodcategory.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemFoodCategoryTabItemBinding
import com.bestapp.zipbab.model.FilterUiState

class TabItemViewHolder(
    parent: ViewGroup,
    private val onFoodTabItemClick: (foodUiState: FilterUiState.FoodUiState, position: Int) -> Unit,
    private val adapter: TabItemAdapter
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_food_category_tab_item, parent, false)
) {

    private val binding = ItemFoodCategoryTabItemBinding.bind(itemView)

    fun bind(
        foodUiState: FilterUiState.FoodUiState
    ) {
        binding.iv.load(foodUiState.icon)
        binding.tv.text = foodUiState.name
        binding.root.isSelected = bindingAdapterPosition == adapter.getSelectIndex()
        binding.root.setOnClickListener {
            // 선택된 Item과 다른 Item 클릭 시 호출
            if (adapter.getSelectIndex() != bindingAdapterPosition) {
                // 새롭게 선택된 View의 isSelected 속성 true
                it.isSelected = true

                // 전에 선택됐던 View의 isSelected 속성 false
                adapter.notifyItemChanged(adapter.getSelectIndex()) // -> onBind 재호출

                // 새롭게 선택된 position값 저장
                adapter.setSelectIndex(bindingAdapterPosition)
            }

            onFoodTabItemClick(foodUiState, bindingAdapterPosition)
        }
    }
}
