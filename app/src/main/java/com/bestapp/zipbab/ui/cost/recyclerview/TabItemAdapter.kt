package com.bestapp.zipbab.ui.cost.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.FilterUiState

class TabItemAdapter(
    private val onCostTabItemClick: (foodUiState: FilterUiState.CostUiState, position: Int) -> Unit,
) : ListAdapter<FilterUiState.CostUiState, TabItemViewHolder>(TabItemDiffUtil()) {

    private var selectIndex = DEFAULT_INDEX

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TabItemViewHolder =
        TabItemViewHolder(parent, onCostTabItemClick, this)

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
