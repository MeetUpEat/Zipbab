package com.bestapp.zipbab.ui.foodcategory.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemFoodCategoryBinding
import com.bestapp.zipbab.model.MeetingUiState

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
        binding.tvTemperature.text = String.format("호스트 온도 : %.0f도", meetingUiState.hostTemperature)
        binding.root.setOnClickListener {
            onFoodCategoryClick(meetingUiState)
        }
    }
}
