package com.bestapp.rice.model

// TODO - 21. sealed로 하지 말고, 한 개의 클래스로 통합하면 좋겠습니다.
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
}