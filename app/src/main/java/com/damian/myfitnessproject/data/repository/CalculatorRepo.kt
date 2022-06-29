package com.damian.myfitnessproject.data.repository

import com.damian.myfitnessproject.data.PreferencesManager
import com.damian.myfitnessproject.ui.calculator.options.*
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculatorRepo @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    val calculatorPreferences = preferencesManager.calculatorFlow.map { data ->
        if (data.calComplete) {
            val summary = calculateSummary(
                data.bmr,
                data.neat,
                data.cardio,
                data.weightLifting
            )
            return@map data.copy(summary = summary)
        } else {
            return@map null
        }
    }

    suspend fun calculateAndStoreBmr(weight: Double, height: Int, age: Int, gender: Gender) {
        val bodyWeightValue = weight * 9.99
        val heightValue = height * 6.25
        val ageValue = age * 4.92
        val bmrBasic = bodyWeightValue + heightValue - ageValue
        val bmr = when (gender) {
            Gender.Man -> bmrBasic + 5
            Gender.Woman -> bmrBasic - 161
        }.toInt()

        preferencesManager.updateBmr(bmr)
    }

    suspend fun calculateAndStoreNeat(bodyType: BodyType, activityLevel: ActivityLevel) {
        val neat: Int
        when (bodyType) {
            BodyType.Ectomorph -> neat = when (activityLevel) {
                ActivityLevel.Lov -> 700
                ActivityLevel.Medium -> 800
                ActivityLevel.High -> 900
            }
            BodyType.Mesomorph -> neat = when (activityLevel) {
                ActivityLevel.Lov -> 200
                ActivityLevel.Medium -> 300
                ActivityLevel.High -> 400
            }
            BodyType.Endomorph -> neat = when (activityLevel) {
                ActivityLevel.Lov -> 400
                ActivityLevel.Medium -> 450
                ActivityLevel.High -> 500
            }
        }
        preferencesManager.updateNeat(neat)
    }

    suspend fun calculateAndStoreCardio(
        sessions: Int,
        duration: Int,
        cardioIntensity: CardioIntensity
    ) {
        val cardio = if (duration != 0 && sessions != 0) {
            val energyUse = when (cardioIntensity) {
                CardioIntensity.Lov -> 3.5
                CardioIntensity.Medium -> 8.5
                CardioIntensity.High -> 11.0
            }
            (sessions * duration * energyUse / 7).toInt()
        } else {
            0
        }
        preferencesManager.updateCardio(cardio)
    }

    suspend fun calculateAndStoreWeightLifting(
        sessions: Int,
        duration: Int,
        intensity: WeightLiftingIntensity
    ) {
        val lifting = if (sessions != 0 && duration != 0) {
            val energyUse = when (intensity) {
                WeightLiftingIntensity.Lov -> 8
                WeightLiftingIntensity.Medium -> 10
                WeightLiftingIntensity.High -> 12
            }
            (sessions * duration * energyUse / 7)
        } else {
            0
        }
        preferencesManager.updateWeightLifting(lifting)
    }

    private fun calculateSummary(bmr: Int, neat: Int, cardio: Int, lifting: Int): Int {
        val summary: Int = bmr + neat + cardio + lifting
        val termEffect = summary * 0.1
        return (summary + termEffect).toInt()
    }

    suspend fun calculationsComplete(isComplete: Boolean) {
        preferencesManager.updateCalComplete(isComplete)
    }

    suspend fun setResultAsTarget(target: Int) {
        preferencesManager.updateTarget(target)
    }
}