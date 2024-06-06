package com.bestapp.rice.model

sealed interface FilterUiState {

    data class FoodUiState(
        val icon: String,
        val name: String,
    ) : FilterUiState

    data class CostUiState(
        val icon: String,
        val name: String,
        val type: Int,
    ) : FilterUiState
//    ) : Parcelable, FilterUiState {
//
//        companion object {
//
//            fun createFrom(costCategory: Filter.Cost) = CostUiState(
//                icon = costCategory.icon,
//                name = costCategory.name,
//                type = costCategory.type
//            )
//        }
//    }
}