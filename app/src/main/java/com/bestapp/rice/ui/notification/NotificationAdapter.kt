package com.bestapp.rice.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bestapp.rice.R

class NotificationAdapter(
    private val itemList : ArrayList<NotificationType>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MAIN = 1
        private const val TYPE_USER = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = itemList[position]
        return when (item) {
            is NotificationType.MainNotification -> TYPE_MAIN
            is NotificationType.UserNotification -> TYPE_USER
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_MAIN -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_notification, parent, false)
                ViewHolderMain(view)
            }
            TYPE_USER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_main_notification, parent, false)
                ViewHolderUser(view)
            }
            else -> {
                throw RuntimeException("오류")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            TYPE_MAIN -> initLayoutMain(holder as ViewHolderMain, position)
            TYPE_USER -> initLayoutUser(holder as ViewHolderUser, position)
            else -> {

            }
        }
    }

    private fun initLayoutMain(holder: ViewHolderMain, pos: Int) {
        val item = itemList[pos] as NotificationType.MainNotification
        holder.titleText.text = item.dec
        holder.date.text = item.uploadDate
    }

    private fun initLayoutUser(holder: ViewHolderUser, pos: Int) {
        val item = itemList[pos] as NotificationType.UserNotification
        holder.titleText2.text = item.dec
        holder.date2.text = item.uploadDate
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolderMain(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = itemView.findViewById<TextView>(R.id.notification_dec)
        val date: TextView = itemView.findViewById<TextView>(R.id.upload_date)
    }

    inner class ViewHolderUser(view: View) : RecyclerView.ViewHolder(view) {
        val titleText2: TextView = itemView.findViewById<TextView>(R.id.notification_dec2)
        val date2: TextView = itemView.findViewById<TextView>(R.id.upload_date2)
    }
}