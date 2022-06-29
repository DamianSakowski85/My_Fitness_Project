package com.damian.myfitnessproject.data.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
@Entity(tableName = "body_measurements")
data class BodyMeasurements(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bodyWeight: Double,
    val waistDiameter: Double,
    val midsectionDiameter: Double,
    val date: String
) : Parcelable {
    @Ignore
    constructor(
        bodyWeight: Double,
        waistDiameter: Double,
        midsectionDiameter: Double,
        date: String
    ) : this(0, bodyWeight, waistDiameter, midsectionDiameter, date)

    val bodyWeightFormatted: String
        get() = bodyWeight.toString()

    val waistDiameterFormatted: String
        get() = waistDiameter.toString()

    val midsectionDiameterFormatted: String
        get() = midsectionDiameter.toString()

    val dateFormatted: String
        get() = formatDate(date)

    private fun formatDate(date: String): String {
        val localDate = LocalDate.parse(date)
        val formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
        return localDate.format(formatter)
    }
}

