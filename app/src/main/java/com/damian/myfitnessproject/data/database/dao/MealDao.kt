package com.damian.myfitnessproject.data.database.dao

import androidx.room.*
import com.damian.myfitnessproject.data.database.entity.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert
    suspend fun insert(meal: Meal)

    @Query("SELECT date FROM meal WHERE date !=:date GROUP BY date")
    suspend fun datesGroupByExcludeDate(date: String): List<String>

    @Delete
    suspend fun delete(meal: Meal)

    @Update
    suspend fun update(meal: Meal)

    @Query("SELECT * FROM meal WHERE date=:date")
    fun getAllAsFlow(date: String): Flow<List<Meal>>

    @Query("DELETE FROM meal WHERE date!=:currentDate")
    suspend fun clearHistory(currentDate: String)
}