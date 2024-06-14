package com.bestapp.zipbab.ui.meetupmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ItemMeetUpListBinding

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

        fun bind(meetUpMapUi: MeetUpMapUi) = with(binding) {
            ivTitleImage.clipToOutline = true
            ivTitleImage.load(meetUpMapUi.titleImage)

            tvTitle.text = meetUpMapUi.title

            val distance = if (meetUpMapUi.distanceByUser < CLASSIFICATION_STANDARD_VALUE) {
                root.context.getString(R.string.meet_up_map_distance_m).format(meetUpMapUi.distanceByUser * UNIT_CONVERSION_MAPPER)
            } else {
                root.context.getString(R.string.meet_up_map_distance_km).format(meetUpMapUi.distanceByUser)
            }
            tvDistance.text = distance

            tvDateTime.text = meetUpMapUi.time
            tvPeopleCount.text = root.context.getString(R.string.meet_up_map_recruits).format(meetUpMapUi.members.size + HOST_COUNT, meetUpMapUi.recruits)
            tvPzipbab.text = root.context.getString(R.string.meet_up_map_pzipbab).format(meetUpMapUi.costValueByPerson)
            tvDescription.text = meetUpMapUi.description
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
        const val HOST_COUNT = 1
        const val UNIT_CONVERSION_MAPPER = 1000
        const val CLASSIFICATION_STANDARD_VALUE = 1.0

        val diff = object : DiffUtil.ItemCallback<MeetUpMapUi>() {
            override fun areItemsTheSame(oldItem: MeetUpMapUi, newItem: MeetUpMapUi) =
                oldItem.meetingDocumentID == newItem.meetingDocumentID

            override fun areContentsTheSame(oldItem: MeetUpMapUi, newItem: MeetUpMapUi) =
                oldItem == newItem
        }
    }
}