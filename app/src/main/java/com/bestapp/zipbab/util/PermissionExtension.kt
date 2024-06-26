package com.bestapp.zipbab.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showAlertDialog(
    title: String,
    message: String,
    negativeMessage: String,
    positiveMessage: String,
    onNegativeSelect: () -> Unit,
    onPositiveSelect: () -> Unit,
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeMessage) { _: DialogInterface, _: Int ->
            onNegativeSelect()
        }
        .setPositiveButton(positiveMessage) { _, _ ->
            onPositiveSelect()
        }
        .show()
}

fun Context.redirectUserToSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
    }
    startActivity(intent)
}