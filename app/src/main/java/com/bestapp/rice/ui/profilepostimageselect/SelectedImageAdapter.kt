package com.bestapp.rice.ui.profilepostimageselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.rice.databinding.ItemSelectedImageBinding
import com.bestapp.rice.ui.profilepostimageselect.model.SelectedImageUiState

class SelectedImageAdapter :
    ListAdapter<SelectedImageUiState, SelectedImageAdapter.SelectedImageViewHolder>(diff) {

    class SelectedImageViewHolder(private val binding: ItemSelectedImageBinding) :
        ViewHolder(binding.root) {

        fun bind(state: SelectedImageUiState) {
            binding.ivThumbnail.load(state)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        return SelectedImageViewHolder(
            ItemSelectedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItem(): List<SelectedImageUiState> {
        val items = mutableListOf<SelectedImageUiState>()

        for (idx in 0 until itemCount) {
            items.add(getItem(idx))
        }
        return items
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<SelectedImageUiState>() {
            override fun areItemsTheSame(
                oldItem: SelectedImageUiState,
                newItem: SelectedImageUiState
            ): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(
                oldItem: SelectedImageUiState,
                newItem: SelectedImageUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}
