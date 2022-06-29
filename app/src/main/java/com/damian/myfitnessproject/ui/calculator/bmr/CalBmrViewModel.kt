package com.damian.myfitnessproject.ui.calculator.bmr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.CalculatorRepo
import com.damian.myfitnessproject.ui.calculator.options.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalBmrViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val calculatorRepo: CalculatorRepo
) : ViewModel() {

    var bodyWeight = savedStateHandle.get<String>("bodyWeight") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("bodyWeight", value)
        }

    var height = savedStateHandle.get<String>("height") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("height", value)
        }

    var age = savedStateHandle.get<String>("age") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("age", value)
        }

    private var gender = savedStateHandle.get<Gender>("gender")
        set(value) {
            field = value
            savedStateHandle.set("gender", value)
        }

    var man = savedStateHandle.get<Boolean>("man") ?: false
        set(value) {
            field = value
            savedStateHandle.set("man", value)
        }

    var woman = savedStateHandle.get<Boolean>("woman") ?: false
        set(value) {
            field = value
            savedStateHandle.set("woman", value)
        }

    fun onManClick() {
        gender = Gender.Man
    }

    fun onWomanClick() {
        gender = Gender.Woman
    }

    fun onNextStepClick() = viewModelScope.launch {
        when {
            bodyWeight.isBlank() -> {
                eventChannel.send(Event.ShowBodyWeightError(R.string.required_error))

                return@launch
            }
            containsToManyPoints(bodyWeight) -> {
                eventChannel.send(Event.ShowBodyWeightError(R.string.to_many_points))
                return@launch
            }
        }
        when {
            height.isBlank() -> {
                eventChannel.send(Event.ShowHeightError(R.string.required_error))
                return@launch
            }
            containsToManyPoints(height) -> {
                eventChannel.send(Event.ShowHeightError(R.string.to_many_points))
                return@launch
            }
        }
        if (age.isBlank()) {
            eventChannel.send(Event.ShowAgeError(R.string.required_error))
            return@launch
        }
        gender?.let {
            kotlin.runCatching {
                val bodyWeightValue = bodyWeight.toDouble()
                val heightValue = height.toInt()
                val ageValue = age.toInt()

                calculatorRepo.calculateAndStoreBmr(bodyWeightValue, heightValue, ageValue, it)
                calculatorRepo.calculationsComplete(false)
            }.onSuccess {
                eventChannel.send(Event.NavToCalNeat)
            }
        } ?: run {
            eventChannel.send(Event.ShowGenderError(R.string.required_error))
        }
    }

    private fun containsToManyPoints(value: String): Boolean {
        val chars = value.toCharArray()
        var contains = false
        chars.forEach {
            if (it == '.') {
                if (contains) {
                    return true
                }
                contains = true
            }
        }
        return false
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object NavToCalNeat : Event()
        data class ShowBodyWeightError(val resId: Int) : Event()
        data class ShowHeightError(val resId: Int) : Event()
        data class ShowAgeError(val resId: Int) : Event()
        data class ShowGenderError(val resId: Int) : Event()
    }
}