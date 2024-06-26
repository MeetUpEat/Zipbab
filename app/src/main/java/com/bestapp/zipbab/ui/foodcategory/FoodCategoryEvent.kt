package com.bestapp.zipbab.ui.foodcategory

sealed interface FoodCategoryEvent {
    data object ScrollEvent : FoodCategoryEvent
}
