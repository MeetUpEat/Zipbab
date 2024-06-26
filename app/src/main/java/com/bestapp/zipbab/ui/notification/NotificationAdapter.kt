package com.bestapp.zipbab.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.databinding.ViewholderMainNotificationBinding
import com.bestapp.zipbab.databinding.ViewholderUserNotificationBinding

class NotificationAdapter(

): ListAdapter<NotificationTypeResponse, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NotificationTypeResponse>() {
            override fun areItemsTheSame(oldItem: NotificationTypeResponse, newItem: NotificationTypeResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NotificationTypeResponse, newItem: NotificationTypeResponse): Boolean {
                return oldItem == newItem
            }
        }

        private const val TYPE_MAIN = 1
        private const val TYPE_USER = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is NotificationTypeResponse.MainResponseNotification -> TYPE_MAIN
            is NotificationTypeResponse.UserResponseNotification -> TYPE_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_MAIN -> {
                val binding = ViewholderMainNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderMain(binding)
            }
            TYPE_USER -> {
                val binding = ViewholderUserNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderUser(binding)
            }
            else -> {
                throw RuntimeException("오류")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder) {
            is ViewHolderMain -> {
                if(item is NotificationTypeResponse.MainResponseNotification) holder.bind(item)
            }
            is ViewHolderUser -> {
                if(item is NotificationTypeResponse.UserResponseNotification) holder.bind(item)
            }
        }
    }

    fun removeItem(itemList: ArrayList<NotificationTypeResponse>, position: Int) {
        val items : ArrayList<NotificationTypeResponse> = itemList
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolderMain(private val binding: ViewholderMainNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mainNotification: NotificationTypeResponse.MainResponseNotification) {
            binding.tDes.text = mainNotification.dec
            binding.notificationDec.text = mainNotification.title
            binding.uploadDate.text = mainNotification.uploadDate
        }
    }

    inner class ViewHolderUser(private val binding: ViewholderUserNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userNotification: NotificationTypeResponse.UserResponseNotification) {
            binding.expandText.text = userNotification.dec
            binding.notificationDec2.text = userNotification.title
            binding.uploadDate2.text = userNotification.uploadDate
        }
    }
}