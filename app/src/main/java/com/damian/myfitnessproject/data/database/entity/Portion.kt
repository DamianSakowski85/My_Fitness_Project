package com.damian.myfitnessproject.data.database.entity

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "portion",
    indices = [Index("foodId"), Index("mealId")],
    foreignKeys = [
        ForeignKey(
            entity = Food::class, parentColumns = arrayOf("id"),
            childColumns = arrayOf("foodId"), onDelete = CASCADE
        ),
        ForeignKey(
            entity = Meal::class, parentColumns = arrayOf("id"),
            childColumns = arrayOf("mealId"), onDelete = CASCADE
        )
    ]
)
data class Portion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val foodId: Int,
    @ColumnInfo val mealId: Int,
    @ColumnInfo val date: String,
    @ColumnInfo val size: Int
) : Parcelable {

    @Ignore
    constructor(
        foodId: Int,
        mealId: Int,
        date: String,
        size: Int
    ) : this(0, foodId, mealId, date, size)
}