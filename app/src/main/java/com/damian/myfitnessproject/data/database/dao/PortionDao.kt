package com.damian.myfitnessproject.data.database.dao

import androidx.room.*
import com.damian.myfitnessproject.data.database.entity.Portion
import kotlinx.coroutines.flow.Flow

@Dao
interface PortionDao {

    @Insert
    suspend fun insert(portion: Portion)

    @Delete
    suspend fun delete(portion: Portion)

    @Update
    suspend fun update(portion: Portion)

    @Query("UPDATE portion SET size=:size WHERE id=:portionId")
    suspend fun update(size: Int, portionId: Int)

    @Query("SELECT size FROM portion WHERE id=:id")
    suspend fun sizeById(id: Int): Int

    @Query("SELECT * FROM portion WHERE date=:date")
    fun getAllByDateFlow(date: String): Flow<List<Portion>>

    @Query("SELECT * FROM portion WHERE mealId=:mealId")
    fun getAllForMealFlow(mealId: Int): Flow<List<Portion>>

    @Query("SELECT foodId FROM portion WHERE mealId=:mealId")
    fun getAllFoodIdsForMeal(mealId: Int): Flow<List<Int>>
}