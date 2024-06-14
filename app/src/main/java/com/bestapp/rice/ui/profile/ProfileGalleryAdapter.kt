package com.bestapp.rice.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemGalleryBinding
import com.bestapp.rice.model.PostUiState
import com.bestapp.rice.model.UploadState

class ProfileGalleryAdapter(
    private val onClick: (PostUiState) -> Unit,
    private val onLongClick: (PostUiState) -> Unit,
) :
    ListAdapter<PostUiState, ProfileGalleryAdapter.ProfileGalleryViewHolder>(diff) {

    class ProfileGalleryViewHolder(
        private val binding: ItemGalleryBinding,
        private val onClick: (PostUiState) -> Unit,
        private val onLongClick: (PostUiState) -> Unit,
    ) : ViewHolder(binding.root) {

        private lateinit var item: PostUiState

        init {
            binding.root.setOnClickListener {
                onClick(item)
            }
            binding.root.setOnLongClickListener {
                onLongClick(item)
                return@setOnLongClickListener true
            }
        }

        fun bind(item: PostUiState) {
            this.item = item
            binding.ivImage.load(item.images.first())

            if (item.state is UploadState.InProgress) {
                binding.vModalBackground.isVisible = true
                binding.cpiLoading.max = item.state.maxOrder
                binding.cpiLoading.progress = item.state.currentProgressOrder
                binding.tvProgress.text = binding.root.context.getString(R.string.progress).format(item.state.currentProgressOrder, item.state.maxOrder)
                binding.cpiLoading.isVisible = true
                binding.tvProgress.isVisible = true
            } else {
                binding.vModalBackground.isVisible = false
                binding.tvProgress.isVisible = false
                binding.cpiLoading.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileGalleryViewHolder {
        return ProfileGalleryViewHolder(
            ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick,
            onLongClick,
        )
    }

    override fun onBindViewHolder(holder: ProfileGalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<PostUiState>() {
            override fun areItemsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
                return oldItem.postDocumentID == newItem.postDocumentID
            }

            override fun areContentsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
                return oldItem == newItem
            }
        }
    }
}