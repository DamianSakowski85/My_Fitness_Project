package com.damian.myfitnessproject.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

data class CalculatorPreferences(
    val bmr: Int,
    val neat: Int,
    val cardio: Int,
    val weightLifting: Int,
    val calComplete: Boolean,
    val summary: Int
)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        const val APP_PREFERENCES = "app_preferences"
        val TARGET = intPreferencesKey("target")
        val BMR = intPreferencesKey("bmr")
        val NEAT = intPreferencesKey("neat")
        val CARDIO = intPreferencesKey("cardio")
        val WEIGHT_LIFTING = intPreferencesKey("weight_lifting")
        val CAL_COMPLETE = booleanPreferencesKey("cal_complete")
    }

    private val Context._dataStore: DataStore<Preferences> by preferencesDataStore(APP_PREFERENCES)
    private val dataStore: DataStore<Preferences> = context._dataStore

    val targetFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[TARGET] ?: 0 }//FilterPreferences(targetValue) }


    val calculatorFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val bmr = preferences[BMR] ?: 0
            val neat = preferences[NEAT] ?: 0
            val cardio = preferences[CARDIO] ?: 0
            val weightLifting = preferences[WEIGHT_LIFTING] ?: 0
            val calComplete = preferences[CAL_COMPLETE] ?: false
            CalculatorPreferences(bmr, neat, cardio, weightLifting, calComplete, 0)
        }

    suspend fun updateTarget(target: Int) {
        dataStore.edit { preferences ->
            preferences[TARGET] = target
        }
    }

    suspend fun updateBmr(bmr: Int) {
        dataStore.edit { preferences -> preferences[BMR] = bmr }
    }

    suspend fun updateNeat(neat: Int) {
        dataStore.edit { preferences -> preferences[NEAT] = neat }
    }

    suspend fun updateCardio(cardio: Int) {
        dataStore.edit { preferences -> preferences[CARDIO] = cardio }
    }

    suspend fun updateWeightLifting(weightLifting: Int) {
        dataStore.edit { preferences -> preferences[WEIGHT_LIFTING] = weightLifting }
    }

    suspend fun updateCalComplete(isComplete: Boolean) {
        dataStore.edit { preferences -> preferences[CAL_COMPLETE] = isComplete }
    }
}