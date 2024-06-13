package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface FilterUi {
    @Parcelize
    data class FoodUi(
        val icon: String,
        val name: String,
    ) : Parcelable, FilterUi

    @Parcelize
    data class CostUi(
        val icon: String,
        val name: String,
        val type: Int,
    ) : Parcelable, FilterUi
}