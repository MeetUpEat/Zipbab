package com.bestapp.zipbab.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.databinding.ItemSearchBinding
import com.bestapp.zipbab.model.MeetingUiState

class SearchAdapter(
    private val onSearchClick: (meetingUiState: MeetingUiState) -> Unit,
) : ListAdapter<MeetingUiState, SearchAdapter.SearchViewHolder>(diff) {

    class SearchViewHolder(
        private val binding: ItemSearchBinding,
        private val onSearchClick: (meetingUiState: MeetingUiState) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meetingUiState: MeetingUiState) {
            binding.root.setOnClickListener {
                onSearchClick(meetingUiState)
            }

            binding.tvTitle.text = meetingUiState.title
            binding.tvLocation.text = meetingUiState.placeLocationUiState.locationAddress
            binding.iv.load(meetingUiState.titleImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onSearchClick,
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<MeetingUiState>() {

            override fun areItemsTheSame(
                oldItem: MeetingUiState,
                newItem: MeetingUiState
            ): Boolean = oldItem.meetingDocumentID == newItem.meetingDocumentID

            override fun areContentsTheSame(
                oldItem: MeetingUiState,
                newItem: MeetingUiState
            ): Boolean = oldItem == newItem
        }
    }
}