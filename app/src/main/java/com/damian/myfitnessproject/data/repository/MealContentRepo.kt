package com.damian.myfitnessproject.data.repository

import com.damian.myfitnessproject.data.database.dao.FoodDao
import com.damian.myfitnessproject.data.database.dao.PortionDao
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.database.entity.Portion
import com.damian.myfitnessproject.data.repository.model.MealContentData
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import com.damian.myfitnessproject.data.repository.model.MealContentSum
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MealContentRepo @Inject constructor(
    private val foodDao: FoodDao,
    private val portionDao: PortionDao
) {

    fun allForMeal(mealId: Int): Flow<List<Portion>> = portionDao.getAllForMealFlow(mealId)

    suspend fun collectData(portions: List<Portion>): MealContentData {
        val foods = ArrayList<MealContentItem>()

        var cal = 0.0
        var fats = 0.0
        var carbs = 0.0
        var proteins = 0.0

        portions.forEach {
            val wholeFood = foodDao.getWholeFood(it.foodId)
            val multiplier = it.size * 0.01

            cal += (multiplier * wholeFood.cal).toInt()
            fats += (multiplier * wholeFood.fats)
            carbs += (multiplier * wholeFood.carbs)
            proteins += (multiplier * wholeFood.proteins)

            val food = MealContentItem(
                wholeFood.id,
                wholeFood.name,
                wholeFood.description,
                it
            )
            foods.add(food)
        }

        val summary = MealContentSum(cal.toInt(), fats, carbs, proteins)

        return MealContentData(foods, summary)
    }

    suspend fun calculateMacroForPortion(mealContentItem: MealContentItem): Food {
        val wholeFood = foodDao.getWholeFood(mealContentItem.id)
        val multiplier = mealContentItem.portion.size * 0.01

        val cal = (multiplier * wholeFood.cal).toInt()
        val fats = (multiplier * wholeFood.fats)
        val carbs = (multiplier * wholeFood.carbs)
        val proteins = (multiplier * wholeFood.proteins)

        return wholeFood.copy(
            cal = cal,
            fats = fats,
            carbs = carbs,
            proteins = proteins
        )
    }

    suspend fun delete(portion: Portion) {
        portionDao.delete(portion)
    }

    suspend fun updatePortion(portion: Portion) {
        portionDao.update(portion)
    }
}