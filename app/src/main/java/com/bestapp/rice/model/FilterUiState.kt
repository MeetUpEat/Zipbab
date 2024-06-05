package com.bestapp.rice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface FilterUiState {

    @Parcelize
    data class FoodUiState(
        val icon: String,
        val name: String,
    ) : Parcelable, FilterUiState

    @Parcelize
    data class CostUiState(
        val icon: String,
        val name: String,
        val type: Int,
    ) : Parcelable, FilterUiState
}