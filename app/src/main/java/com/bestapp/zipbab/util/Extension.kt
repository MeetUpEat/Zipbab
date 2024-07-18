package com.bestapp.zipbab.util

 import android.widget.ImageView
 import androidx.annotation.DrawableRes
 import androidx.fragment.app.Fragment
 import androidx.navigation.NavDirections
 import androidx.navigation.fragment.findNavController
 import coil.load
 import com.bestapp.zipbab.R

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

fun NavDirections.safeNavigate(origin: Fragment) {
    if (origin.parentFragmentManager.fragments.last() != origin) {
        return
    }
    origin.findNavController().navigate(this)
}