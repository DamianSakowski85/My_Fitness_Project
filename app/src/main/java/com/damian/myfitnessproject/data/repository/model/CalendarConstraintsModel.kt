package com.damian.myfitnessproject.data.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class CalendarConstraintsModel(
    val dates : List<DateModel>,
    val startDate: Calendar,
    val endDate: Calendar,
    val previousDate: String
) : Parcelable
