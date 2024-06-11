package com.bestapp.rice.ui.recruitment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ViewholderImageselectBinding

class ImageSelectAdapter : ListAdapter<ImageSelect, ImageSelectAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ImageSelect>() {
            override fun areItemsTheSame(oldItem: ImageSelect, newItem: ImageSelect): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ImageSelect, newItem: ImageSelect): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding : ViewholderImageselectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageSelect: ImageSelect) {
            binding.ivMain.load(imageSelect.image) {
                placeholder(R.drawable.sample_profile_image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderImageselectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}