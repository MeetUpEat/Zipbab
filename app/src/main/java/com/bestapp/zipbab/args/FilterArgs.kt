package com.bestapp.zipbab.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface FilterArgs {
    @Parcelize
    data class FoodArgs(
        val icon: String,
        val name: String,
    ) : Parcelable, FilterArgs

    @Parcelize
    data class CostArgs(
        val icon: String,
        val name: String,
        val type: Int,
    ) : Parcelable, FilterArgs
}