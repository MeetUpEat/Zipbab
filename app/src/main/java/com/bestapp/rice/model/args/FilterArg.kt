package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface FilterArg {
    @Parcelize
    data class FoodArg(
        val icon: String,
        val name: String,
    ) : Parcelable, FilterArg

    @Parcelize
    data class CostArg(
        val icon: String,
        val name: String,
        val type: Int,
    ) : Parcelable, FilterArg
}