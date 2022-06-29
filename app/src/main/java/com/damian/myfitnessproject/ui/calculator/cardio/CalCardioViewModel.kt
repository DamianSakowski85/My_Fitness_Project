package com.damian.myfitnessproject.ui.calculator.cardio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.CalculatorRepo
import com.damian.myfitnessproject.ui.calculator.options.CardioIntensity
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalCardioViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val calculatorRepo: CalculatorRepo
) : ViewModel() {

    var sessions = savedStateHandle.get<String>("sessions") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("sessions", value)
        }

    var duration = savedStateHandle.get<String>("duration") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("duration", value)
        }

    var lov = savedStateHandle.get<Boolean>("lov") ?: false
        set(value) {
            field = value
            savedStateHandle.set("lov", value)
        }

    var medium = savedStateHandle.get<Boolean>("medium") ?: false
        set(value) {
            field = value
            savedStateHandle.set("medium", value)
        }

    var high = savedStateHandle.get<Boolean>("high") ?: false
        set(value) {
            field = value
            savedStateHandle.set("high", value)
        }

    fun onLovIntenseClick() {
        intensityLevel = CardioIntensity.Lov
    }

    fun onMediumIntenseClick() {
        intensityLevel = CardioIntensity.Medium
    }

    fun onHighIntenseClick() {
        intensityLevel = CardioIntensity.High
    }

    private var intensityLevel = savedStateHandle.get<CardioIntensity>("intensityLevel")
        set(value) {
            field = value
            savedStateHandle.set("intensityLevel", value)
        }

    fun onSkipCardioClick() = viewModelScope.launch {
        eventChannel.send(Event.NavToCalWeightLifting)
    }

    fun onNextStepClick() = viewModelScope.launch {
        when {
            sessions.isBlank() -> {
                eventChannel.send(Event.ShowSessionsError(R.string.required_error))
                return@launch
            }
            duration.isBlank() -> {
                eventChannel.send(Event.ShowDurationError(R.string.required_error))
                return@launch
            }
        }
        intensityLevel?.let {
            kotlin.runCatching {
                calculatorRepo.calculateAndStoreCardio(sessions.toInt(), duration.toInt(), it)
            }.onSuccess {
                eventChannel.send(Event.NavToCalWeightLifting)
            }
        } ?: run {
            eventChannel.send(Event.ShowIntensityError)
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object NavToCalWeightLifting : Event()
        data class ShowSessionsError(val resId: Int) : Event()
        data class ShowDurationError(val resId: Int) : Event()
        object ShowIntensityError : Event()
    }
}