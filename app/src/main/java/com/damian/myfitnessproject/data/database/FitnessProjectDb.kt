package com.damian.myfitnessproject.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.damian.myfitnessproject.data.database.dao.BodyMeasurementsDao
import com.damian.myfitnessproject.data.database.dao.FoodDao
import com.damian.myfitnessproject.data.database.dao.MealDao
import com.damian.myfitnessproject.data.database.dao.PortionDao
import com.damian.myfitnessproject.data.database.entity.BodyMeasurements
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.database.entity.Portion
import com.damian.myfitnessproject.hilt.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [Food::class, Meal::class, Portion::class, BodyMeasurements::class],
    version = 1
)
abstract class FitnessProjectDb : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun portionDao(): PortionDao
    abstract fun bodyMeasurementsDao(): BodyMeasurementsDao

    class Callback @Inject constructor(
        private val database: Provider<FitnessProjectDb>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            applicationScope.launch {
                pop(database.get())
            }
        }

        private suspend fun pop(database: FitnessProjectDb?) {
            database?.let {
                val foods = FoodDbData()
                foods.getFoodData().forEach {
                    database.foodDao().insert(it)
                }
            }
        }
    }
}