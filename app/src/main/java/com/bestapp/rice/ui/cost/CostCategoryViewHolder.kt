package com.bestapp.rice.ui.cost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemCostCategoryBinding
import com.bestapp.rice.model.MeetingUiState


class CostCategoryViewHolder(
    parent: ViewGroup,
    private val onCostCategoryClick: (meetingUiState: MeetingUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_cost_category, parent, false)
) {

    private val binding = ItemCostCategoryBinding.bind(itemView)

    fun bind(
        meetingUiState: MeetingUiState
    ) {
        binding.iv.load(meetingUiState.titleImage)
        binding.tvTitle.text = meetingUiState.title
        binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
        binding.tvTemperature.text = String.format("가격: %d원", meetingUiState.costValueByPerson)
        binding.root.setOnClickListener {
            onCostCategoryClick(meetingUiState)
        }
    }
}