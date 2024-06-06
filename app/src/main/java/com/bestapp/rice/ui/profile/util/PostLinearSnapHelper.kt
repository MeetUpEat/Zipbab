package com.bestapp.rice.ui.profile.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class PostLinearSnapHelper : PagerSnapHelper() {

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val view = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val linearLayoutManager = layoutManager as LinearLayoutManager

        val beforePosition = linearLayoutManager.findFirstVisibleItemPosition()
        val nextPosition = linearLayoutManager.findLastVisibleItemPosition()

        var currentPosition = linearLayoutManager.getPosition(view)

        // 넘기는 속도에 따라 다음 아이템 또는 이전 아이템이 표시되도록 설정
        if (velocityX > MIN_FLIPPING_VELOCITY) {
            currentPosition = nextPosition
        } else if (velocityX < -MIN_FLIPPING_VELOCITY) {
            currentPosition = beforePosition
        }

        return currentPosition
    }

    fun getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }

    companion object {
        private const val MIN_FLIPPING_VELOCITY = 100
    }
}