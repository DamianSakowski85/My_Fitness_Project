package com.damian.myfitnessproject.data.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateModel(
    val year: Int,
    val month : Int,
    val day : Int
): Parcelable
