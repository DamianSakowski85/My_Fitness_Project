package com.damian.myfitnessproject.ui.bodyMeasurements.addEdit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.ADD_BODY_MEASUREMENTS
import com.damian.myfitnessproject.EDIT_BODY_MEASUREMENTS
import com.damian.myfitnessproject.data.database.entity.BodyMeasurements
import com.damian.myfitnessproject.data.repository.BodyMeasRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditMeasurementsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val bodyMeasRepo: BodyMeasRepo
) : ViewModel() {

    private val _measurements = savedStateHandle.get<BodyMeasurements>("bodyMeasurements")

    var bodyWeight =
        savedStateHandle.get<String>("bodyWeight") ?: _measurements?.bodyWeight?.toString() ?: ""
        set(value) {
            field = value
            savedStateHandle.set("bodyWeight", value)
        }

    var waistDiameter =
        savedStateHandle.get<String>("waistDiameter") ?: _measurements?.waistDiameter?.toString()
        ?: ""
        set(value) {
            field = value
            savedStateHandle.set("waistDiameter", value)
        }

    var midSectionDiameter = savedStateHandle.get<String>("midSectionDiameter")
        ?: _measurements?.midsectionDiameter?.toString() ?: ""
        set(value) {
            field = value
            savedStateHandle.set("midSectionDiameter", value)
        }

    fun onSaveButtonClick() = viewModelScope.launch {
        if (bodyWeightIsCorrect() && waistDiameterIsCorrect() && midSecDiameterIsCorrect()) {
            val bodyWeightDouble = bodyWeight.toDouble()
            val waistDiameterDouble = waistDiameter.toDouble()
            val midSecDiameterDouble = midSectionDiameter.toDouble()

            _measurements?.let {
                kotlin.runCatching {
                    val bodyMeasurements = it.copy(
                        bodyWeight = bodyWeightDouble,
                        waistDiameter = waistDiameterDouble,
                        midsectionDiameter = midSecDiameterDouble
                    )
                    bodyMeasRepo.update(bodyMeasurements)
                }.onSuccess {
                    eventChannel.send(
                        Event.NavBackWithResult(
                            EDIT_BODY_MEASUREMENTS
                        )
                    )
                }
            } ?: kotlin.runCatching {
                val bodyMeasurements = BodyMeasurements(
                    bodyWeightDouble,
                    waistDiameterDouble,
                    midSecDiameterDouble,
                    LocalDate.now().toString()
                )
                bodyMeasRepo.insert(bodyMeasurements)
            }.onSuccess {
                eventChannel.send(
                    Event.NavBackWithResult(
                        ADD_BODY_MEASUREMENTS
                    )
                )
            }
        }
    }

    private suspend fun bodyWeightIsCorrect(): Boolean {
        return when {
            bodyWeight.isBlank() -> {
                eventChannel.send(Event.ShowBodyWeightError(R.string.required_error))
                false
            }
            containsToManyPoints(bodyWeight) -> {
                eventChannel.send(Event.ShowBodyWeightError(R.string.to_many_points))
                false
            }
            else -> {
                eventChannel.send(Event.ShowBodyWeightError(R.string.empty))
                true
            }
        }
    }

    private suspend fun waistDiameterIsCorrect(): Boolean {
        return when {
            waistDiameter.isBlank() -> {
                eventChannel.send(Event.ShowWaistDiameterError(R.string.required_error))
                false
            }
            containsToManyPoints(waistDiameter) -> {
                eventChannel.send(Event.ShowWaistDiameterError(R.string.to_many_points))
                false
            }
            else -> {
                eventChannel.send(Event.ShowWaistDiameterError(R.string.empty))
                true
            }
        }
    }

    private suspend fun midSecDiameterIsCorrect(): Boolean {
        return when {
            midSectionDiameter.isBlank() -> {
                eventChannel.send(Event.ShowMidSecDiameterError(R.string.required_error))
                false
            }
            containsToManyPoints(midSectionDiameter) -> {
                eventChannel.send(Event.ShowMidSecDiameterError(R.string.to_many_points))
                false
            }
            else -> {
                eventChannel.send(Event.ShowMidSecDiameterError(R.string.empty))
                true
            }
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
        data class ShowBodyWeightError(val resId: Int) : Event()
        data class ShowWaistDiameterError(val resId: Int) : Event()
        data class ShowMidSecDiameterError(val resId: Int) : Event()
        data class NavBackWithResult(val result: Int) : Event()
    }
}