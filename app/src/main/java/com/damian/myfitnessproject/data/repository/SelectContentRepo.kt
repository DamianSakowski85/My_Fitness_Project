package com.damian.myfitnessproject.data.repository

import com.damian.myfitnessproject.data.database.dao.FoodDao
import com.damian.myfitnessproject.data.database.dao.PortionDao
import com.damian.myfitnessproject.data.database.entity.Portion
import com.damian.myfitnessproject.data.repository.model.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SelectContentRepo @Inject constructor(
    private val foodDao: FoodDao,
    private val portionDao: PortionDao
) {

    suspend fun insert(portion: Portion) {
        portionDao.insert(portion)
    }

    private fun allPortionIdsForMeal(mealId: Int): Flow<List<Int>> =
        portionDao.getAllFoodIdsForMeal(mealId)

    private suspend fun collectFoods(foodIds: List<Int>, searchQuery: String): List<FoodItem> {
        return foodDao.getAllFoodItemsWhereNotIn(foodIds, searchQuery)
    }

    fun test(mealId: Int, query: String) =
        allPortionIdsForMeal(mealId).map { ids -> collectFoods(ids, query) }
}