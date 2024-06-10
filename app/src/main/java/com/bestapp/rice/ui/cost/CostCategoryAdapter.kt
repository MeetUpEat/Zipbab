package com.bestapp.rice.ui.cost

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.rice.model.MeetingUiState


class CostCategoryAdapter(
    private val onCostCategoryClick: (meetingUiState: MeetingUiState) -> Unit,
) : ListAdapter<MeetingUiState, CostCategoryViewHolder>(CostCategoryDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CostCategoryViewHolder =
        CostCategoryViewHolder(parent = parent, onCostCategoryClick = onCostCategoryClick)

    override fun onBindViewHolder(holder: CostCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}