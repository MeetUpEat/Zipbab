package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.FilterResponse

interface CategoryRepository {
    suspend fun getFoodCategory(): List<FilterResponse.Food>

    suspend fun getCostCategory(): List<FilterResponse.Cost>
}