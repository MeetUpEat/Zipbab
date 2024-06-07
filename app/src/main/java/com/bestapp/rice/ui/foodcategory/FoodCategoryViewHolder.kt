package com.bestapp.rice.ui.foodcategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemFoodCategoryBinding
import com.bestapp.rice.model.MeetingUiState

class FoodCategoryViewHolder(
    parent: ViewGroup,
    private val onFoodCategoryClick: (meetingUiState: MeetingUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_food_category, parent, false)
) {

    private val binding = ItemFoodCategoryBinding.bind(itemView)

    fun bind(
        meetingUiState: MeetingUiState
    ) {
        binding.iv.load(meetingUiState.titleImage)
        binding.tvTitle.text = meetingUiState.title
        binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
        binding.tvTemperature.text = String.format("온도 %f도", meetingUiState.hostTemperature.toString())
        binding.root.setOnClickListener {
            onFoodCategoryClick(meetingUiState)
        }
    }
}
