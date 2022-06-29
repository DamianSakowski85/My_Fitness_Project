package com.damian.myfitnessproject.ui.calculator.lifting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.CalculatorRepo
import com.damian.myfitnessproject.ui.calculator.options.WeightLiftingIntensity
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalLiftingViewModel @Inject constructor(
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
        intensityLevel = WeightLiftingIntensity.Lov
    }

    fun onMediumIntenseClick() {
        intensityLevel = WeightLiftingIntensity.Medium
    }

    fun onHighIntenseClick() {
        intensityLevel = WeightLiftingIntensity.High
    }

    private var intensityLevel = savedStateHandle.get<WeightLiftingIntensity>("intensityLevel")
        set(value) {
            field = value
            savedStateHandle.set("intensityLevel", value)
        }

    fun onSkipWeightLiftingClick() = viewModelScope.launch {
        calculatorRepo.calculationsComplete(true)
        eventChannel.send(Event.NavToSummary)
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
                calculatorRepo.calculateAndStoreWeightLifting(
                    sessions.toInt(),
                    duration.toInt(),
                    it
                )
                calculatorRepo.calculationsComplete(true)
            }.onSuccess {
                eventChannel.send(Event.NavToSummary)
            }
        } ?: run {
            eventChannel.send(Event.ShowIntensityError)
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object NavToSummary : Event()
        data class ShowSessionsError(val resId: Int) : Event()
        data class ShowDurationError(val resId: Int) : Event()
        object ShowIntensityError : Event()
    }
}