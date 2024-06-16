package com.bestapp.zipbab.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bestapp.zipbab.model.MeetingUiState

class SearchAdapter(
    private val onSearchClick: (meetingUiState: MeetingUiState) -> Unit,
) : ListAdapter<MeetingUiState, SearchViewHolder>(SearchDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder = SearchViewHolder(parent = parent, onSearchClick = onSearchClick)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}