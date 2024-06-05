package com.bestapp.rice.model

import androidx.annotation.DrawableRes
import com.bestapp.rice.R

enum class MeetingBadge(@DrawableRes val drawableRes: Int, val minCount: Int) {
    NEWBIE(R.drawable.newbie_badge, 0),
    SENIOR(R.drawable.senior_badge, 6),
    MASTER(R.drawable.master_badge, 20);

    companion object {
        fun from(meetCount: Int): MeetingBadge {
            return when {
                meetCount >= MASTER.minCount -> MASTER
                meetCount >= SENIOR.minCount -> SENIOR
                else -> NEWBIE
            }
        }
    }
}