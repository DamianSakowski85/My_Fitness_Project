package com.damian.myfitnessproject.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.damian.myfitnessproject.data.database.entity.BodyMeasurements
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementsDao {
    @Insert
    suspend fun insert(bodyMeasurements: BodyMeasurements)

    @Update
    suspend fun update(bodyMeasurements: BodyMeasurements)

    @Query("DELETE FROM body_measurements")
    suspend fun deleteAll()

    @Query("SELECT * FROM body_measurements WHERE date=:date")
    fun getByDate(date: String): Flow<BodyMeasurements?>

    @Query("SELECT * FROM body_measurements WHERE date!=:currentDate ORDER BY id DESC LIMIT 1")
    fun getLastFromToday(currentDate: String): Flow<BodyMeasurements?>

    @Query("SELECT * FROM body_measurements")
    fun getAll(): Flow<List<BodyMeasurements>>
}