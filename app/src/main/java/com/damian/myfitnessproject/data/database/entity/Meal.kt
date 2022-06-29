package com.damian.myfitnessproject.data.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "meal")
@Parcelize
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val date: String,
    @ColumnInfo val time: String
) : Parcelable {
    constructor(
        name: String,
        date: String,
        time: String
    ) : this(0, name, date, time)

    val dateFormatted: String
        get() = formatDate(date)

    private fun formatDate(date: String): String {
        val localDate = LocalDate.parse(date)
        val formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
        return localDate.format(formatter)
    }
}


