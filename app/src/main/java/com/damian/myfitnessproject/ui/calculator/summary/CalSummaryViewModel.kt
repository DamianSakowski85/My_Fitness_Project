package com.damian.myfitnessproject.ui.calculator.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.CalculatorRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalSummaryViewModel @Inject constructor(
    private val calculatorRepo: CalculatorRepo
) : ViewModel() {

    val currentCalResult = calculatorRepo.calculatorPreferences.asLiveData()

    fun onDoneClick() = viewModelScope.launch {
        eventChannel.send(Event.NavBackToStart)
    }

    fun onSetResultAsTargetClick() = viewModelScope.launch {
        currentCalResult.value?.summary?.let { calculatorRepo.setResultAsTarget(it) }
        eventChannel.send(Event.NavBackToStart)
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object NavBackToStart : Event()
    }
}