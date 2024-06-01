package com.bestapp.rice.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.rice.databinding.ItemPostImageBinding
import com.bestapp.rice.model.ImageUiState

class PostAdapter : ListAdapter<ImageUiState, PostAdapter.PostViewHolder>(diff) {

    class PostViewHolder(private val binding: ItemPostImageBinding) : ViewHolder(binding.root) {

        fun bind(item: ImageUiState) {
            binding.ivImage.load(item.url)
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<ImageUiState>() {
            override fun areItemsTheSame(oldItem: ImageUiState, newItem: ImageUiState): Boolean {
                return oldItem.imageDocumentId == newItem.imageDocumentId
            }

            override fun areContentsTheSame(oldItem: ImageUiState, newItem: ImageUiState): Boolean {
                return oldItem == newItem
            }

        }
    }
}