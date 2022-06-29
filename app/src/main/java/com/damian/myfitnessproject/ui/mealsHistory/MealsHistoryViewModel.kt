package com.damian.myfitnessproject.ui.mealsHistory

import androidx.lifecycle.*
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.MealsHistoryRepo
import com.damian.myfitnessproject.data.repository.model.CalendarConstraintsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MealsHistoryViewModel @Inject constructor(
    private val mealsHistoryRepo: MealsHistoryRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _constraints = MutableLiveData<CalendarConstraintsModel>()
    val calendarConstraints: LiveData<CalendarConstraintsModel> = _constraints


    private val searchDate = savedStateHandle.getLiveData("searchDate", "")

    private val foodFlow = searchDate.asFlow().flatMapLatest {
        mealsHistoryRepo.getAllMeals(it)
    }
    val allMeals = foodFlow.asLiveData()


    private val _summary = searchDate.asFlow().flatMapLatest {
        mealsHistoryRepo.summary(it)
    }
    val summary = _summary.asLiveData()


    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    init {
        viewModelScope.launch {
            val data = mealsHistoryRepo.loadCalendarConstraints()
            data?.let {
                _constraints.value = it

                searchDate.value?.let { query ->
                    when {
                        query.isBlank() -> onPickDate(data.previousDate)
                        query.isNotBlank() -> onPickDate(query)
                    }
                }
            }

        }
    }

    fun onDatePickerShowClick() = viewModelScope.launch {
        eventChannel.send(Event.ShowDatePicker)
    }

    fun onPickDate(date: String) {
        _title.value = formatDate(date)
        searchDate.value = date
    }

    fun onItemClick(meal: Meal) = viewModelScope.launch {
        eventChannel.send(Event.NavToDetails(meal))
    }

    fun onDeleteAllClick() = viewModelScope.launch {
        eventChannel.send(Event.NavToDeleteAll)
    }

    private fun formatDate(date: String): String {
        val localDate = LocalDate.parse(date)
        val formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
        return localDate.format(formatter)
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        object ShowDatePicker : Event()
        object NavToDeleteAll : Event()
        data class NavToDetails(val meal: Meal) : Event()
    }
}