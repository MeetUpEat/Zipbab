package com.bestapp.rice.ui.profileimageselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.rice.databinding.ItemProfileImageSelectGalleryBinding

class ProfileImageSelectAdapter :
    ListAdapter<GalleryImageInfo, ProfileImageSelectAdapter.ProfileImageSelectViewHolder>(diff) {

    class ProfileImageSelectViewHolder(private val binding: ItemProfileImageSelectGalleryBinding) :
        ViewHolder(binding.root) {

        fun bind(item: GalleryImageInfo) {
            binding.ivThumbnail.load(item.uri) {
//                placeholder() // 로딩 중임을 나타내는 Vector 직접 그려볼 예정
            }
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
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileImageSelectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<GalleryImageInfo>() {
            override fun areItemsTheSame(oldItem: GalleryImageInfo, newItem: GalleryImageInfo): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: GalleryImageInfo, newItem: GalleryImageInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}