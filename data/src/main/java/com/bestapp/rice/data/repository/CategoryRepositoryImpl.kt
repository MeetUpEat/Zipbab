package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Filter

class CategoryRepositoryImpl: CategoryRepository {
    override suspend fun getFoodCategory(): List<Filter> {
        TODO("Not yet implemented")
    }

    override suspend fun getCostCategory(): List<Filter> {
        TODO("Not yet implemented")
    }
}