package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Filter

interface CategoryRepository {
    suspend fun getFoodCategory() : List<Filter>

    suspend fun getCostCategory() : List<Filter>
}