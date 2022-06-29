package com.damian.myfitnessproject.ui.meals.addEdit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.ADD_MEAL_RESULT_OK
import com.damian.myfitnessproject.EDIT_MEAL_RESULT_OK
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.MealRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditMealViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mealRepo: MealRepo
) : ViewModel() {

    private var _meal: Meal? = null

    init {
        _meal = savedStateHandle.get<Meal>("meal")
    }

    var name = savedStateHandle.get<String>("mealName") ?: _meal?.name ?: ""
        set(value) {
            field = value
            savedStateHandle.set("mealName", value)
        }

    fun onSaveClick() = viewModelScope.launch {
        if (name.isBlank()) {
            eventChannel.send(Event.ShowNameError(R.string.required_error))
        } else {
            _meal?.let {
                kotlin.runCatching {
                    val meal = it.copy(name = name)
                    mealRepo.update(meal)
                }.onSuccess {
                    eventChannel.send(Event.NavigateBackWithResult(EDIT_MEAL_RESULT_OK))
                }
            } ?: run {
                kotlin.runCatching {
                    val date = LocalDate.now().toString()
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    val meal = Meal(name, date, time)
                    mealRepo.insert(meal)
                }.onSuccess {
                    eventChannel.send(Event.NavigateBackWithResult(ADD_MEAL_RESULT_OK))
                }
            }
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowNameError(val resId: Int) : Event()
        data class NavigateBackWithResult(val result: Int) : Event()
    }
}