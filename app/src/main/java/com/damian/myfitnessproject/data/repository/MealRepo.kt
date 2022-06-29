package com.damian.myfitnessproject.data.repository

import com.damian.myfitnessproject.data.PreferencesManager
import com.damian.myfitnessproject.data.database.dao.FoodDao
import com.damian.myfitnessproject.data.database.dao.MealDao
import com.damian.myfitnessproject.data.database.dao.PortionDao
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.database.entity.Portion
import com.damian.myfitnessproject.data.repository.model.MealsSum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealRepo @Inject constructor(
    private val mealDao: MealDao,
    private val portionDao: PortionDao,
    private val foodDao: FoodDao,
    private val preferencesManager: PreferencesManager
) {
    private fun allPortionsByDateFlow(date: String): Flow<List<Portion>> =
        portionDao.getAllByDateFlow(date)

    private val preferencesFlow = preferencesManager.targetFlow

    fun allMeals(date: String): Flow<List<Meal>> = mealDao.getAllAsFlow(date)

    fun summary(date: String) = combine(
        allPortionsByDateFlow(date).map { portions -> calMealsSummary(portions) },
        preferencesFlow
    ) { summary, pref ->
        summary.apply {
            target = pref
            left = calLeftCal(this.cal, pref)
        }
        summary
    }

    suspend fun updateTargetPref(target: Int) {
        preferencesManager.updateTarget(target)
    }

    suspend fun insert(meal: Meal) {
        mealDao.insert(meal)
    }

    suspend fun delete(meal: Meal) {
        mealDao.delete(meal)
    }

    suspend fun update(meal: Meal) {
        mealDao.update(meal)
    }

    private suspend fun calMealsSummary(portions: List<Portion>): MealsSum {
        val mealsSummary = MealsSum()

        portions.forEach {
            val wholeFood = foodDao.getWholeFood(it.foodId)
            val multiplier = it.size * 0.01
            mealsSummary.apply {
                cal += (multiplier * wholeFood.cal).toInt()
                fats += (multiplier * wholeFood.fats)
                carbs += (multiplier * wholeFood.carbs)
                proteins += (multiplier * wholeFood.proteins)
            }
        }

        return mealsSummary
    }

    private fun calLeftCal(cal: Int, target: Int): Int {
        val left = target - cal
        return if (left < 0) {
            0
        } else {
            left
        }
    }
}