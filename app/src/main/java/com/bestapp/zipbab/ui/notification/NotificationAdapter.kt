package com.bestapp.zipbab.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bestapp.zipbab.data.model.remote.NotificationType
import com.bestapp.zipbab.databinding.ViewholderMainNotificationBinding
import com.bestapp.zipbab.databinding.ViewholderUserNotificationBinding

class NotificationAdapter(

): ListAdapter<NotificationType, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NotificationType>() {
            override fun areItemsTheSame(oldItem: NotificationType, newItem: NotificationType): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NotificationType, newItem: NotificationType): Boolean {
                return oldItem == newItem
            }
        }

        private const val TYPE_MAIN = 1
        private const val TYPE_USER = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is NotificationType.MainNotification -> TYPE_MAIN
            is NotificationType.UserNotification -> TYPE_USER
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
                if(item is NotificationType.MainNotification) holder.bind(item)
            }
            is ViewHolderUser -> {
                if(item is NotificationType.UserNotification) holder.bind(item)
            }
        }
    }

    fun removeItem(itemList: ArrayList<NotificationType>, position: Int) {
        val items : ArrayList<NotificationType> = itemList
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolderMain(private val binding: ViewholderMainNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mainNotification: NotificationType.MainNotification) {
            binding.tDes.text = mainNotification.dec
            binding.notificationDec.text = mainNotification.title
            binding.uploadDate.text = mainNotification.uploadDate
        }
    }

    inner class ViewHolderUser(private val binding: ViewholderUserNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userNotification: NotificationType.UserNotification) {
            binding.expandText.text = userNotification.dec
            binding.notificationDec2.text = userNotification.title
            binding.uploadDate2.text = userNotification.uploadDate
        }
    }
}