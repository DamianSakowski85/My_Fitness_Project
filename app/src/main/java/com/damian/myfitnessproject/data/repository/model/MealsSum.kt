package com.damian.myfitnessproject.data.repository.model

import java.math.BigDecimal

data class MealsSum(
    var cal: Int,
    var target: Int,
    var left: Int,
    var fats: Double,
    var carbs: Double,
    var proteins: Double
) {
    constructor() : this(0, 0, 0, 0.0, 0.0, 0.0)

    val calFormatted: String
        get() = cal.toString()

    val targetFormatted: String
        get() = target.toString()

    val leftFormatted: String
        get() = left.toString()

    val fatsFormatted: String
        get() = doubleFormatter(fats)

    val carbsFormatted: String
        get() = doubleFormatter(carbs)

    val proteinsFormatted: String
        get() = doubleFormatter(proteins)

    private fun doubleFormatter(value: Double): String {
        if (value == 0.0 || value == 0.0) {
            return "0.0"
        }
        val bd = BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP)
        val formattedValue = bd.toDouble()
        return formattedValue.toString()
    }
}
