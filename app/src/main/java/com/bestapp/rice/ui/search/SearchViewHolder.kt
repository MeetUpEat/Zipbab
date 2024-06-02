package com.bestapp.rice.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemSearchBinding
import com.bestapp.rice.model.MeetingUiState

class SearchViewHolder(
    parent: ViewGroup,
    private val onSearchClick: (meetingUiState: MeetingUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
) {

    private val binding = ItemSearchBinding.bind(itemView)


    fun bind(
        meetingUiState: MeetingUiState
    ) {

        binding.tvTitle.text = meetingUiState.title
        binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
        binding.iv.load(R.drawable.ic_launcher_background)
        binding.root.setOnClickListener {
            onSearchClick(meetingUiState)
        }
    }
}
