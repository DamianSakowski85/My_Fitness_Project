package com.damian.myfitnessproject.data.repository.model

import android.os.Parcelable
import com.damian.myfitnessproject.data.database.entity.Portion
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealContentItem(
    val id: Int,
    val name: String,
    val description: String,
    val portion: Portion
) : Parcelable