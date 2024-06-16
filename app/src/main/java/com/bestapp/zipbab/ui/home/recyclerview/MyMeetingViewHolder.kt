package com.bestapp.zipbab.ui.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemMyMeetBinding
import com.bestapp.zipbab.model.MeetingUiState

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
        binding.iv.clipToOutline = true
        binding.iv.load(meetingUiState.titleImage)
        binding.tvTitle.text = meetingUiState.title
        binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
        binding.root.setOnClickListener {
            onMyMeetingClick(meetingUiState)
        }
    }
}
