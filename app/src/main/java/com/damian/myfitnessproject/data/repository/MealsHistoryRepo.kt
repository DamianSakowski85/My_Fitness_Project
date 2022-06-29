package com.damian.myfitnessproject.data.repository

import com.damian.myfitnessproject.data.database.dao.FoodDao
import com.damian.myfitnessproject.data.database.dao.MealDao
import com.damian.myfitnessproject.data.database.dao.PortionDao
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.database.entity.Portion
import com.damian.myfitnessproject.data.repository.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class MealsHistoryRepo @Inject constructor(
    private val mealDao: MealDao,
    private val portionDao: PortionDao,
    private val foodDao: FoodDao
) {

    fun getAllMeals(date: String) = mealDao.getAllAsFlow(date)

    suspend fun loadCalendarConstraints(): CalendarConstraintsModel? {
        val currentDay = LocalDate.now().toString()
        val calendarConstList = ArrayList<DateModel>()
        val dates = mealDao.datesGroupByExcludeDate(currentDay)
        if (dates.isNotEmpty()) {
            val startDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val endDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            dates.forEach {
                val parsedDate = LocalDate.parse(it)

                val year = parsedDate.year
                val month = parsedDate.monthValue - 1
                val day = parsedDate.dayOfMonth

                calendarConstList.add(DateModel(year,month,day))

                if (it == dates.first()) {
                    startDate.set(year, month, day)
                }
                if (it == dates.last()) {
                    endDate.set(year, month, day)
                }
            }

            return CalendarConstraintsModel(calendarConstList, startDate, endDate, dates.last())
        }

        return null
    }

    private fun allPortionsByDateFlow(date: String): Flow<List<Portion>> =
        portionDao.getAllByDateFlow(date)

    fun summary(date: String) =
        allPortionsByDateFlow(date).map { portions -> calMealsSummary(portions) }

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


    //content history
    private fun allPortionsForMeal(mealId: Int): Flow<List<Portion>> =
        portionDao.getAllForMealFlow(mealId)

    private suspend fun collectContentData(portions: List<Portion>): MealContentData {
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

    fun mealSummary(meal: Meal) =
        allPortionsForMeal(meal.id).map { portions -> collectContentData(portions) }

    suspend fun clearHistory() {
        val currentDate = LocalDate.now().toString()
        mealDao.clearHistory(currentDate)
    }
}