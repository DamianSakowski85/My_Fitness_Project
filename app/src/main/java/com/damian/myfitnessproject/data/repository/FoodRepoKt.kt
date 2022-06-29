package com.damian.myfitnessproject.data.repository

import android.graphics.Bitmap
import com.damian.myfitnessproject.data.database.dao.FoodDao
import com.damian.myfitnessproject.data.database.dao.PortionDao
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.repository.model.FoodItem
import javax.inject.Inject


class FoodRepoKt @Inject constructor(
    private val foodDao: FoodDao,
    private val portionDao: PortionDao,
    private val imageRepo: ImageRepo
) {

    fun getAllItems(searchQuery: String) = foodDao.getAllItems(searchQuery)

    suspend fun getWholeFood(id: Int): Food {
        return foodDao.getWholeFood(id)
    }

    suspend fun size(portionId: Int) = portionDao.sizeById(portionId)

    suspend fun insert(food: Food) {
        foodDao.insert(food)
    }

    suspend fun insert(foodToInsert: Food, bitmap: Bitmap) {
        val imagePath = imageRepo.saveToInternalStorage(bitmap, foodToInsert.name)
        val food = foodToInsert.copy(imagePath = imagePath)
        foodDao.insert(food)
    }

    suspend fun update(food: Food) {
        foodDao.update(food)
    }

    suspend fun update(foodToUpdate: Food, bitmap: Bitmap) {
        if (foodToUpdate.imagePath.isNotBlank()) {
            imageRepo.deleteBitmap(foodToUpdate.imagePath)
        }
        val imagePath = imageRepo.saveToInternalStorage(bitmap, foodToUpdate.name)
        val food = foodToUpdate.copy(imagePath = imagePath)
        foodDao.update(food)
    }

    suspend fun delete(foodItem: FoodItem) {
        if (foodItem.imagePath.isNotBlank()) {
            imageRepo.deleteBitmap(foodItem.imagePath)
        }

        foodDao.deleteById(foodItem.id)
    }

    fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap =
        imageRepo.convertByteArrayToBitmap(byteArray)
}