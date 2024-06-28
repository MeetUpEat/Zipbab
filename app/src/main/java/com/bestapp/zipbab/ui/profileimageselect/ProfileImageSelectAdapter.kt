package com.bestapp.zipbab.ui.profileimageselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.zipbab.data.model.local.GalleryImageInfo
import com.bestapp.zipbab.databinding.ItemProfileImageSelectGalleryBinding

class ProfileImageSelectAdapter(
    private val onClick: (GalleryImageInfo) -> Unit,
) : PagingDataAdapter<GalleryImageInfo, ProfileImageSelectAdapter.ProfileImageSelectViewHolder>(diff) {

    class ProfileImageSelectViewHolder(
        private val binding: ItemProfileImageSelectGalleryBinding,
        private val onClick: (GalleryImageInfo) -> Unit,
    ) : ViewHolder(binding.root) {

        private var item: GalleryImageInfo = GalleryImageInfo.empty()

        init {
            binding.root.setOnClickListener {
                onClick(item)
            }
        }

        fun bind(item: GalleryImageInfo) {
            this.item = item
            binding.ivThumbnail.load(item.uri)
            binding.tvName.text = item.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileImageSelectViewHolder {
        return ProfileImageSelectViewHolder(
            ItemProfileImageSelectGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick,
        )
    }

    override fun onBindViewHolder(holder: ProfileImageSelectViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<GalleryImageInfo>() {
            override fun areItemsTheSame(
                oldItem: GalleryImageInfo,
                newItem: GalleryImageInfo
            ): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(
                oldItem: GalleryImageInfo,
                newItem: GalleryImageInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}