package com.bestapp.rice.ui.mettinginfo

import android.os.SystemClock
import android.view.View

class OnSingleClickListener(
    private val onClickListener: (view: View) -> Unit
) : View.OnClickListener{

    // 처음 클릭 클릭 시간 기록
    private var lastClickedTime = 0L

    override fun onClick(view: View) {
        val onClickedTime = SystemClock.elapsedRealtime()
        if ((onClickedTime-lastClickedTime) < INTERVAL) { return }
        lastClickedTime = onClickedTime
        onClickListener.invoke(view)
    }

    companion object {

        // 버튼 사이에 허용하는 시간간격
        const val INTERVAL = 3000L
    }
}