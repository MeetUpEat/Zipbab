package com.bestapp.rice.ui.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemMyMeetBinding
import com.bestapp.rice.model.MeetingUiState

class MyMeetingViewHolder(
    parent: ViewGroup,
    private val onMyMeetingClick: (meetingUiState: MeetingUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_my_meet, parent, false)
) {

    private val binding = ItemMyMeetBinding.bind(itemView)

    fun bind(
        meetingUiState: MeetingUiState
    ) {
        binding.iv.load(meetingUiState.titleImage)
        binding.tvTitle.text = meetingUiState.title
        binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
        binding.root.setOnClickListener {
            onMyMeetingClick(meetingUiState)
        }
    }
}
