package com.damian.myfitnessproject.ui.calculator.start

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
class CalStartViewModel @Inject constructor(
    calculatorRepo: CalculatorRepo
) : ViewModel() {

    val currentCalResult = calculatorRepo.calculatorPreferences.asLiveData()

    fun onStartCalculatorClick() = viewModelScope.launch {
        eventChannel.send(Event.NavToCalBmr)
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object NavToCalBmr : Event()
    }
}