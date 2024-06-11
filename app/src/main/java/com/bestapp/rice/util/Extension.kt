package com.bestapp.rice.util

 import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import com.bestapp.rice.R

fun ImageView.loadOrDefault(
    imageUrl: String?,
    @DrawableRes defaultRes: Int = R.drawable.sample_profile_image
) {
    if (imageUrl.isNullOrBlank()) {
        this.load(defaultRes)
        return
    }
    this.load(imageUrl) {
        placeholder(defaultRes)
    }
}