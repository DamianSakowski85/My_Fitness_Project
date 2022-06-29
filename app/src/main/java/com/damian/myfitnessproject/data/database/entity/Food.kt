package com.damian.myfitnessproject.data.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "food")
@Parcelize
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val cal: Int,
    val fats: Double,
    val carbs: Double,
    val proteins: Double,
    val imagePath: String,
) : Parcelable {

    @Ignore
    constructor(
        name: String,
        description: String,
        cal: Int,
        fats: Double,
        carbs: Double,
        proteins: Double,
        imagePath: String
    ) : this(0, name, description, cal, fats, carbs, proteins, imagePath)

    @Ignore
    constructor(
        name: String,
        description: String,
        cal: Int,
        fats: Double,
        carbs: Double,
        proteins: Double
    ) : this(0, name, description, cal, fats, carbs, proteins, "")
}

