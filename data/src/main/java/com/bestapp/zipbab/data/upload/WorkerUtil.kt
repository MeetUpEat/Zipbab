package com.bestapp.zipbab.data.upload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.bestapp.zipbab.data.R

private const val UPLOAD_NOTIFICATION_ID = 0
private const val UPLOAD_NOTIFICATION_CHANNEL_ID = "UploadNotificationChannel"
fun Context.uploadWorkNotification(message: String): ForegroundInfo {
    val channel = NotificationChannel(
        UPLOAD_NOTIFICATION_CHANNEL_ID,
        getString(R.string.upload_work_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.upload_work_notification_channel_description)
    }
    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    return ForegroundInfo(
        UPLOAD_NOTIFICATION_ID,
        NotificationCompat.Builder(
            this,
            UPLOAD_NOTIFICATION_CHANNEL_ID,
        ).setSmallIcon(
            R.drawable.app_icon
        ).setContentTitle(getString(R.string.upload_work_notification_title))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    )
}

fun Context.makeStatusNotification(message: String) {
    val channel = NotificationChannel(
        UPLOAD_NOTIFICATION_CHANNEL_ID,
        getString(R.string.upload_work_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.upload_work_notification_channel_description)
    }
    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    val builder = NotificationCompat.Builder(
        this,
        UPLOAD_NOTIFICATION_CHANNEL_ID,
    ).setSmallIcon(
        R.drawable.app_icon
    ).setContentTitle(getString(R.string.upload_work_notification_title))
        .setContentText(message)
        .setSilent(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notificationManager?.notify(UPLOAD_NOTIFICATION_ID, builder.build())
}