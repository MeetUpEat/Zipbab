package com.bestapp.rice.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.ItemGalleryBinding
import com.bestapp.rice.model.PostUiState

class ProfileGalleryAdapter(
    private val onClick: (PostUiState) -> Unit,
) :
    ListAdapter<PostUiState, ProfileGalleryAdapter.ProfileGalleryViewHolder>(diff) {

    enum class Location {
        START, MIDDLE, END;

        companion object {
            fun get(position: Int): Location {
                return entries[position % entries.size]
            }
        }
    }

    class ProfileGalleryViewHolder(
        private val binding: ItemGalleryBinding,
        private val onClick: (PostUiState) -> Unit,
    ) : ViewHolder(binding.root) {

        private var location: Location? = null
        private lateinit var item: PostUiState

        private val marginSize =
            binding.root.context.resources.getDimension(R.dimen.default_margin8).toInt()
        private val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams

        init {
            binding.root.setOnClickListener {
                onClick(item)
            }
        }

        fun bind(item: PostUiState, location: Location) {
            this.item = item
            binding.ivImage.load(item.imageUiStates.first().url)

            setMargin(location)
        }

        private fun setMargin(location: Location) {
            if (this.location == location) {
                return
            }

            when (location) {
                Location.START -> {
                    layoutParams.marginStart = 0
                    layoutParams.marginEnd = marginSize
                }

                Location.MIDDLE -> {
                    layoutParams.marginStart = marginSize / 2
                    layoutParams.marginEnd = marginSize / 2
                }

                Location.END -> {
                    layoutParams.marginStart = marginSize
                    layoutParams.marginEnd = 0
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileGalleryViewHolder {
        return ProfileGalleryViewHolder(
            ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick,
        )
    }

    override fun onBindViewHolder(holder: ProfileGalleryViewHolder, position: Int) {
        val location = Location.get(position)
        holder.bind(getItem(position), location)
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