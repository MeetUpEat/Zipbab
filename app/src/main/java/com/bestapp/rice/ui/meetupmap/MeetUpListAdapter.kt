package com.bestapp.rice.ui.meetupmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.databinding.ItemMeetUpListBinding
import com.bestapp.rice.model.args.MeetingUi

class MeetUpListAdapter(
    private val clickListener: (Int) -> Unit,
) : ListAdapter<MeetingUi, MeetUpListAdapter.MeetUpListViewHolder>(diff) {

    class MeetUpListViewHolder(
        private val binding: ItemMeetUpListBinding,
        private val clickListener: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    clickListener(position)
                }
            }
        }

        fun bind(meetingArg: MeetingUi) {
            binding.ivTitleImage.clipToOutline = true
            binding.ivTitleImage.load(meetingArg.titleImage)
            binding.tvTitle.text = meetingArg.title
            binding.tvDateTime.text = meetingArg.time
            binding.tvPeopleCount.text =
                String.format("%d/%d명", meetingArg.members.size, meetingArg.recruits)
            binding.tvPrice.text = String.format("%,d원", meetingArg.costValueByPerson)
            binding.tvDescription.text = meetingArg.description
        }
    }

    override fun onBindViewHolder(holder: MeetUpListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetUpListViewHolder {
        val binding = ItemMeetUpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MeetUpListViewHolder(binding, clickListener)
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<MeetingUi>() {
            override fun areItemsTheSame(oldItem: MeetingUi, newItem: MeetingUi) =
                oldItem.meetingDocumentID == newItem.meetingDocumentID

            override fun areContentsTheSame(oldItem: MeetingUi, newItem: MeetingUi) =
                oldItem == newItem
        }
    }
}