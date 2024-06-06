package com.bestapp.rice.util

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import com.bestapp.rice.R

fun View.setVisibility(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun ImageView.loadOrDefault(
    imageUrl: String?,
    @DrawableRes defaultRes: Int = R.drawable.sample_profile_image,
) {
    this.load(imageUrl) {
        placeholder(defaultRes)
    }
}