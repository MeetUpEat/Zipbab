package com.bestapp.zipbab.ui.meetingmanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemMemberBinding
import com.bestapp.zipbab.model.UserUiState

class MeetingMemberViewHolder(
    parent: ViewGroup,
    private val onMemberClick: (userUiState: UserUiState) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
) {

    private val binding = ItemMemberBinding.bind(itemView)

    fun bind(
        userUiState: UserUiState
    ) {

        binding.ivImg.load(userUiState.profileImage)
        binding.tvMembserName.text = userUiState.nickname
        binding.root.setOnClickListener {
            onMemberClick(userUiState)
        }
    }
}