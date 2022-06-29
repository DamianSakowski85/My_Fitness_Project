package com.damian.myfitnessproject.ui.bodyMeasurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
class BodyMeasurementsViewModel @Inject constructor(
    bodyMeasRepo: BodyMeasRepo
) : ViewModel() {

    val getLast = bodyMeasRepo.getLast(LocalDate.now().toString()).asLiveData()
    val getCurrent = bodyMeasRepo.getCurrent(LocalDate.now().toString()).asLiveData()

    fun onAddEditResult(result: Int) = viewModelScope.launch {
        when (result) {
            ADD_BODY_MEASUREMENTS -> eventChannel.send(Event.ShowMessage(R.string.body_meas_added))
            EDIT_BODY_MEASUREMENTS -> eventChannel.send(Event.ShowMessage(R.string.body_meas_updated))
        }
    }

    fun onAddEditButtonClick() = viewModelScope.launch {

        getCurrent.value?.let {
            eventChannel.send(Event.NavToEdit(it))
        } ?: run {
            eventChannel.send(Event.NavToAdd)
        }
    }

    fun onShowChartsButtonClick() = viewModelScope.launch {
        eventChannel.send(Event.NavToCharts)
    }

    fun onDeleteAllClick() = viewModelScope.launch {
        eventChannel.send(Event.NavToDeleteAll)
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowMessage(val resId: Int) : Event()
        object NavToAdd : Event()
        object NavToCharts : Event()
        data class NavToEdit(val bodyMeasurements: BodyMeasurements) : Event()
        object NavToDeleteAll : Event()
    }
}