package com.bestapp.zipbab.ui.foodcategory.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.FilterUiState

class TabItemAdapter(
    private val onFoodTabItemClick: (foodUiState: FilterUiState.FoodUiState, position: Int) -> Unit,
) : ListAdapter<FilterUiState.FoodUiState, TabItemViewHolder>(TabItemDiffUtil()) {

    private var selectIndex = DEFAULT_INDEX

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TabItemViewHolder =
        TabItemViewHolder(parent, onFoodTabItemClick, this)

    override fun onBindViewHolder(holder: TabItemViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
    fun setSelectIndex(position: Int){
        selectIndex = position
    }
    fun getSelectIndex(): Int{
       return selectIndex
    }
    companion object{
        private const val DEFAULT_INDEX = 0
    }

}
