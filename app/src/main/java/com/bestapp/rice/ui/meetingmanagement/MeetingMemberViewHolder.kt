package com.bestapp.rice.ui.meetingmanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemFoodMenuBinding
import com.bestapp.rice.model.FilterUiState
import com.bestapp.rice.model.UserUiState

class MeetingMemberViewHolder(
    parent: ViewGroup,
    private val onCostClick: (userUiState: UserUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_cost, parent, false)
) {

    private val binding = ItemFoodMenuBinding.bind(itemView)

    fun bind(
        userUiState: UserUiState
    ) {

//        binding.iv.load(userUiState)
//        binding.tv.text = userUiState.name
//        binding.root.setOnClickListener {
//            onCostClick(userUiState)
//        }
    }
}