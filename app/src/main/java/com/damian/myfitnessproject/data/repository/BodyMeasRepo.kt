package com.damian.myfitnessproject.data.repository

import com.damian.myfitnessproject.data.database.dao.BodyMeasurementsDao
import com.damian.myfitnessproject.data.database.entity.BodyMeasurements
import javax.inject.Inject

class BodyMeasRepo @Inject constructor(
    private val bodyMeasurementsDao: BodyMeasurementsDao
) {
    suspend fun insert(bodyMeasurements: BodyMeasurements) =
        bodyMeasurementsDao.insert(bodyMeasurements)

    suspend fun update(bodyMeasurements: BodyMeasurements) =
        bodyMeasurementsDao.update(bodyMeasurements)

    suspend fun deleteAll() = bodyMeasurementsDao.deleteAll()

    fun getAll() = bodyMeasurementsDao.getAll()

    fun getLast(date: String) = bodyMeasurementsDao.getLastFromToday(date)

    fun getCurrent(date: String) = bodyMeasurementsDao.getByDate(date)
}