package com.bestapp.zipbab.ui.profilepostimageselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.zipbab.databinding.ItemPostImageGalleryBinding
import com.bestapp.zipbab.ui.profilepostimageselect.model.PostGalleryUiState

class PostGalleryAdapter(
    private val onClick: (PostGalleryUiState) -> Unit,
) : ListAdapter<PostGalleryUiState, PostGalleryAdapter.PostGalleryViewHolder>(diff) {

    class PostGalleryViewHolder(
        private val binding: ItemPostImageGalleryBinding,
        private val onClick: (PostGalleryUiState) -> Unit,
    ) : ViewHolder(binding.root) {

        private lateinit var state: PostGalleryUiState

        init {
            binding.root.setOnClickListener {
                onClick(state)
            }
        }

        fun bind(state: PostGalleryUiState) {
            this.state = state

            binding.ivThumbnail.load(state.uri)
            binding.tvName.text = state.name

            binding.vEdgeForSelect.isVisible = state.isSelected()
            binding.tvSelectedOrder.isVisible = state.isSelected()
            binding.tvSelectedOrder.text = state.order.toString()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostGalleryViewHolder {
        return PostGalleryViewHolder(
            ItemPostImageGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick,
        )
    }

    override fun onBindViewHolder(holder: PostGalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<PostGalleryUiState>() {
            override fun areItemsTheSame(
                oldItem: PostGalleryUiState,
                newItem: PostGalleryUiState
            ): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(
                oldItem: PostGalleryUiState,
                newItem: PostGalleryUiState
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}