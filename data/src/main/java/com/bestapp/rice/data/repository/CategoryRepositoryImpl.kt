package com.bestapp.rice.data.repository

import android.util.Log
import com.bestapp.rice.data.model.remote.Filter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class CategoryRepositoryImpl(
    private val categoryDB: CollectionReference,
) : CategoryRepository {
    override suspend fun getFoodCategory(): List<Filter.Food>? {
        val documentSnapshot = categoryDB.document("food")
            .get()
            .await()

        val foodList = mutableListOf<Filter.Food>()
        val foods = documentSnapshot.get("food") as? List<Map<String, String>> ?: emptyList()

        for (food in foods) {
            val name = food["name"] ?: ""
            val icon = food["icon"] ?: ""
            foodList.add(Filter.Food(icon, name))
        }

        return foodList
    }

    override suspend fun getCostCategory(): List<Filter.Cost>? {
        val documentSnapshot = categoryDB.document("cost")
            .get()
            .await()

        val costList = mutableListOf<Filter.Cost>()
        val costs = documentSnapshot.get("cost") as? List<Map<String, String>> ?: emptyList()

        for (food in costs) {
            val name = food["name"] ?: ""
            val type = food["type"] ?: ""
            costList.add(Filter.Cost(name, type.toInt()))
        }

        return costList
    }

    /**
     *  데이터 넣는 용도로 사용함
     */
    suspend fun putFoodCategory(foods: Map<String, List<Filter.Food>>) {
        categoryDB.document("food").set(
            foods, SetOptions.merge()
        ).await()
    }

    /**
     *  데이터 넣는 용도로 사용함
     */
    suspend fun putCostCategory(costs: Map<String, List<Filter.Cost>>) {
        categoryDB.document("cost").set(
            costs, SetOptions.merge()
        ).await()
    }
}