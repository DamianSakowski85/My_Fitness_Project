package com.damian.myfitnessproject.data.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MonthModel(
    val month : Int,
    val days : List<Int>
) : Parcelable
