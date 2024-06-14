package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.model.remote.Filter

interface CategoryRepository {
    suspend fun getFoodCategory(): List<Filter.Food>

    suspend fun getCostCategory(): List<Filter.Cost>
}