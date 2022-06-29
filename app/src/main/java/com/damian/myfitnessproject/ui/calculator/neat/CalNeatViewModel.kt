package com.damian.myfitnessproject.ui.calculator.neat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.CalculatorRepo
import com.damian.myfitnessproject.ui.calculator.options.ActivityLevel
import com.damian.myfitnessproject.ui.calculator.options.BodyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalNeatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val calculatorRepo: CalculatorRepo
) : ViewModel() {

    var ectomorph = savedStateHandle.get<Boolean>("ectomorph") ?: false
        set(value) {
            field = value
            savedStateHandle.set("ectomorph", value)
        }

    var endomorph = savedStateHandle.get<Boolean>("endomorph") ?: false
        set(value) {
            field = value
            savedStateHandle.set("endomorph", value)
        }

    var mesomorph = savedStateHandle.get<Boolean>("mesomorph") ?: false
        set(value) {
            field = value
            savedStateHandle.set("mesomorph", value)
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

    private var bodyType = savedStateHandle.get<BodyType>("bodyType")
        set(value) {
            field = value
            savedStateHandle.set("bodyType", value)
        }
    private var actLevel = savedStateHandle.get<ActivityLevel>("actLevel")
        set(value) {
            field = value
            savedStateHandle.set("actLevel", value)
        }

    fun onEctomorphClick() {
        bodyType = BodyType.Ectomorph
    }

    fun onEndomorphClick() {
        bodyType = BodyType.Endomorph
    }

    fun onMesomorphClick() {
        bodyType = BodyType.Mesomorph
    }

    fun onLovActLevelClick() {
        actLevel = ActivityLevel.Lov
    }

    fun onMedActLevelClick() {
        actLevel = ActivityLevel.Medium
    }

    fun onHighActLevelClick() {
        actLevel = ActivityLevel.High
    }

    fun onNextStepClick() = viewModelScope.launch {
        when {
            bodyType == null -> {
                eventChannel.send(Event.ShowBodyTypeError)
                return@launch
            }
            actLevel == null -> {
                eventChannel.send(Event.ShowActivityError)
                return@launch
            }
            else -> {
                kotlin.runCatching {
                    calculatorRepo.calculateAndStoreNeat(bodyType!!, actLevel!!)
                }.onSuccess {
                    eventChannel.send(Event.NavToCalCardio)
                }
            }
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object NavToCalCardio : Event()
        object ShowBodyTypeError : Event()
        object ShowActivityError : Event()
    }
}