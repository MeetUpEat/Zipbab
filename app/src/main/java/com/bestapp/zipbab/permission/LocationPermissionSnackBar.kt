package com.bestapp.zipbab.permission

import androidx.fragment.app.Fragment
import com.bestapp.zipbab.R
import com.google.android.material.snackbar.Snackbar
import android.app.ActionBar
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import androidx.core.view.setPadding

class LocationPermissionSnackBar(
    private val fragment: Fragment
) {
    private val snackBar: Snackbar by lazy {
        Snackbar.make(
            fragment.requireView(),
            fragment.requireContext().getString(R.string.meet_up_map_no_permission),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    fun showPermissionSettingSnackBar() {
        snackBar.apply {
            setStyleAndAction()
        }

        val isShown = snackBar.isShown
        if (!isShown) {
            snackBar.show()
        }
    }

    private fun Snackbar.setStyleAndAction() {
        animationMode = Snackbar.ANIMATION_MODE_FADE

        setAction(
            fragment.requireContext().getString(R.string.meet_up_map_set_permission)
        ) {
            val settingPermissionIntent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:" + fragment.requireActivity().baseContext.packageName)
                }
            fragment.startActivity(settingPermissionIntent)
        }

        view.setBackgroundColor(getColorFromResources(R.color.snackbar_background))
        setTextColor(getColorFromResources(R.color.snackbar_text))
        setActionTextColor(getColorFromResources(R.color.main_color))
        setAnchorViewTopGravity()
    }

    private fun getColorFromResources(color: Int): Int {
        return fragment.resources.getColor(
            color,
            fragment.requireContext().theme
        )
    }

    private fun Snackbar.setAnchorViewTopGravity() {
        val layoutParams = ActionBar.LayoutParams(view.layoutParams)
        layoutParams.gravity = Gravity.TOP

        view.setPadding(12)
        view.layoutParams = layoutParams
    }
}