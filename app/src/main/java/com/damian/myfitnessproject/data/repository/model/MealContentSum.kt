package com.damian.myfitnessproject.data.repository.model

import java.math.BigDecimal

data class MealContentSum(
    val cal: Int,
    val fats: Double,
    val carbs: Double,
    val proteins: Double
) {

    val calFormatted: String
        get() = cal.toString()

    val fatsFormatted: String
        get() = doubleFormatter(fats)

    val carbsFormatted: String
        get() = doubleFormatter(carbs)

    val proteinsFormatted: String
        get() = doubleFormatter(proteins)

    private fun doubleFormatter(value: Double): String {
        if (value == 0.0) {
            return "0.0"
        }
        val bd = BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP)
        val formattedValue = bd.toDouble()
        return formattedValue.toString()
    }
}