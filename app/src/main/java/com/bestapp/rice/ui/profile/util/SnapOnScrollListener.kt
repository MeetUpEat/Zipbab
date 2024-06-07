package com.bestapp.rice.ui.profile.util

import androidx.recyclerview.widget.RecyclerView

class SnapOnScrollListener(
    private val snapHelper: PostLinearSnapHelper,
    private val onSnapPositionChangeListener: OnSnapPositionChangeListener
) : RecyclerView.OnScrollListener() {

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        notifySnapPositionChange(recyclerView)
    }

    private fun notifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        if (this.snapPosition != snapPosition) {
            onSnapPositionChangeListener.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }
}