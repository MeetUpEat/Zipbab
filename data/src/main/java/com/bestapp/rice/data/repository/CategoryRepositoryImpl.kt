package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Filter
import com.google.firebase.firestore.CollectionReference

class CategoryRepositoryImpl(
    private val categoryDB: CollectionReference,
) : CategoryRepository {
    override suspend fun getFoodCategory(): List<Filter> {

    }

    override suspend fun getCostCategory(): List<Filter> {

    }
}