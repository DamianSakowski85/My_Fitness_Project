package com.damian.myfitnessproject.data.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String
) : Parcelable
