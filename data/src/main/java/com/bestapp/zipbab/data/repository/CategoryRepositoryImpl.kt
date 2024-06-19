package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.FirestorDB.FirestoreDB
import com.bestapp.zipbab.data.model.remote.FilterResponse
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class CategoryRepositoryImpl @Inject constructor(
    private val firestoreDB : FirestoreDB
) : CategoryRepository {
    override suspend fun getFoodCategory(): List<FilterResponse.Food> {
        val documentSnapshot = firestoreDB.getCategoryDB().document(FOOD)
            .get()
            .await()

        val foodList = mutableListOf<FilterResponse.Food>()

        val foods =
            try {
                documentSnapshot.get(FOOD) as List<Map<String, String>>
            } catch (e: Exception) {
                DUMMY_DATA[COST]
            }

        for (food in foods!!) {
            val name = food["name"] ?: ""
            val icon = food["icon"] ?: ""
            foodList.add(FilterResponse.Food(icon, name))
        }

        return foodList
    }

    override suspend fun getCostCategory(): List<FilterResponse.Cost> {
        val documentSnapshot = firestoreDB.getCategoryDB().document(COST)
            .get()
            .await()

        val costList = mutableListOf<FilterResponse.Cost>()
        val costs =
            try {
                documentSnapshot.get(COST) as List<Map<String, String>>
            } catch (e: Exception) {
                DUMMY_DATA[COST]
            }

        for (cost in costs!!) {
            val icon: String = cost["icon"] ?: ""
            val name: String = cost["name"] ?: ""
            val type: String = cost["type"] ?: ""
            costList.add(FilterResponse.Cost(icon, name, type))
        }

        return costList
    }

    /**
     *  데이터 넣는 용도로 사용함
     */
    suspend fun putFoodCategory(foods: Map<String, List<FilterResponse.Food>>) {
        firestoreDB.getCategoryDB().document(FOOD).set(
            foods, SetOptions.merge()
        ).await()
    }

    /**
     *  데이터 넣는 용도로 사용함
     */
    suspend fun putCostCategory(costs: Map<String, List<FilterResponse.Cost>>) {
        firestoreDB.getCategoryDB().document(COST).set(
            costs, SetOptions.merge()
        ).await()
    }

    companion object {
        private val FOOD = "food"
        private val COST = "cost"

        private val DUMMY_DATA = mapOf(
            FOOD to List<Map<String, String>>(8) {
                mapOf("name" to "", "type" to "")
            },
            COST to List<Map<String, String>>(4) {
                mapOf("name" to "", "icon" to "", "type" to "")
            },
        )
    }
}