package com.bestapp.rice.ui.meetupmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.databinding.ItemMeetUpListBinding

class MeetUpListAdapter(
    private val clickListener: (Int) -> Unit,
) : ListAdapter<MeetUpMapUi, MeetUpListAdapter.MeetUpListViewHolder>(diff) {

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

        fun bind(meetUpMapUi: MeetUpMapUi) {
            binding.ivTitleImage.clipToOutline = true
            binding.ivTitleImage.load(meetUpMapUi.titleImage)

            binding.tvTitle.text = meetUpMapUi.title

            val distance = if (meetUpMapUi.distanceByUser < 1.0) {
                String.format("%.0fm", meetUpMapUi.distanceByUser * 1000)
            } else {
                String.format("%.1fkm", meetUpMapUi.distanceByUser)
            }
            binding.tvDistance.text = distance

            binding.tvDateTime.text = meetUpMapUi.time
            binding.tvPeopleCount.text =
                String.format("%d/%d명", meetUpMapUi.members.size + HOST_COUNT, meetUpMapUi.recruits)
            binding.tvPrice.text = String.format("%,d원", meetUpMapUi.costValueByPerson)
            binding.tvDescription.text = meetUpMapUi.description
        }
    }

    override fun onBindViewHolder(holder: MeetUpListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetUpListViewHolder {
        val binding =
            ItemMeetUpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MeetUpListViewHolder(binding, clickListener)
    }

    companion object {
        val HOST_COUNT = 1
        val diff = object : DiffUtil.ItemCallback<MeetUpMapUi>() {
            override fun areItemsTheSame(oldItem: MeetUpMapUi, newItem: MeetUpMapUi) =
                oldItem.meetingDocumentID == newItem.meetingDocumentID

            override fun areContentsTheSame(oldItem: MeetUpMapUi, newItem: MeetUpMapUi) =
                oldItem == newItem
        }
    }
}