package com.bestapp.zipbab.ui.mettinginfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bestapp.zipbab.databinding.ItemMemberBinding
import com.bestapp.zipbab.util.loadOrDefault

class MeetingMemberAdapter(
    private val onMemberClick: (memberInfo: MemberInfo) -> Unit,
) : ListAdapter<MemberInfo, MeetingMemberAdapter.MeetingMemberViewHolder>(diff) {

    class MeetingMemberViewHolder(
        private val binding: ItemMemberBinding,
        private val onMemberClick: (memberInfo: MemberInfo) -> Unit,
    ) : ViewHolder(binding.root) {

        fun bind(memberInfo: MemberInfo) {
            binding.root.setOnClickListener {
                onMemberClick(memberInfo)
            }

            binding.ivHostBadge.isVisible = memberInfo.isHost
            binding.ivProfile.loadOrDefault(memberInfo.profileImage)
            binding.tvMemberName.text = memberInfo.nickname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingMemberViewHolder {
        return MeetingMemberViewHolder(
            ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onMemberClick,
        )
    }

    override fun onBindViewHolder(holder: MeetingMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<MemberInfo>() {
            override fun areItemsTheSame(oldItem: MemberInfo, newItem: MemberInfo): Boolean {
                return oldItem.userDocumentID == newItem.userDocumentID
            }

            override fun areContentsTheSame(oldItem: MemberInfo, newItem: MemberInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}