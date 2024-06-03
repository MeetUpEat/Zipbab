package com.bestapp.rice.model

import android.os.Parcelable
import com.bestapp.rice.data.model.remote.Filter
import kotlinx.parcelize.Parcelize

interface FilterUiState {

    @Parcelize
    data class FoodUiState(
        val icon: String,
        val name: String,
    ) : Parcelable, FilterUiState {

        companion object {

            fun createFrom(foodCategory: Filter.Food) = FoodUiState(
                icon = foodCategory.icon,
                name = foodCategory.name,
            )
        }
    }

    @Parcelize
    data class CostUiState(
        val name: String,
        val type: Int,
    ) : Parcelable, FilterUiState {

        companion object {

            fun createFrom(costCategory: Filter.Cost) = CostUiState(
                name = costCategory.name,
                type = costCategory.type
            )
        }
    }
}