package com.damian.myfitnessproject.data.database.dao

import androidx.room.*
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.repository.model.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert
    suspend fun insert(food: Food)

    @Delete
    suspend fun delete(food: Food)

    @Query("DELETE FROM food WHERE id=:id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun update(food: Food)

    @Query("SELECT id,name,description,imagePath FROM food  WHERE name LIKE '%' || :searchQuery || '%' AND  id NOT IN (:ids) ORDER BY name")
    suspend fun getAllFoodItemsWhereNotIn(ids: List<Int>, searchQuery: String): List<FoodItem>

    @Query("SELECT id,name,description,imagePath FROM food WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun getAllItems(searchQuery: String): Flow<List<FoodItem>>


    @Query("SELECT * FROM food WHERE id=:id")
    suspend fun getWholeFood(id: Int): Food
}