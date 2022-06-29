package com.damian.myfitnessproject.ui.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.ADD_MEAL_RESULT_OK
import com.damian.myfitnessproject.EDIT_MEAL_RESULT_OK
import com.damian.myfitnessproject.EDIT_TARGET_RESULT_OK
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.MealRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    mealRepo: MealRepo
) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _currentDate: String = LocalDate.now().toString()
    val currentMeals = mealRepo.allMeals(_currentDate).asLiveData()
    val summary = mealRepo.summary(_currentDate).asLiveData()

    fun onDeleteItemClick(meal: Meal) = viewModelScope.launch {
        eventChannel.send(Event.NavToDelete(meal))
    }

    fun onAddMealClick() = viewModelScope.launch {
        eventChannel.send(Event.NavToAddMeal(R.string.add_meal))
    }

    fun onChangeNameClick(meal: Meal) = viewModelScope.launch {
        eventChannel.send(Event.NavToEditMeal(R.string.edit_meal, meal))
    }

    fun onChangeTargetClick() = viewModelScope.launch {
        val target = summary.value?.target.toString()
        eventChannel.send(Event.NavToChangeTarget(target))
    }

    fun onMealClick(meal: Meal) = viewModelScope.launch {
        eventChannel.send(Event.NavToMealContent(meal.name, meal))
    }

    fun onAddEditResult(result: Int) = viewModelScope.launch {
        when (result) {
            ADD_MEAL_RESULT_OK -> eventChannel.send(
                Event.ShowSavedConfirmationMessage(R.string.meal_added)
            )
            EDIT_MEAL_RESULT_OK -> eventChannel.send(
                Event.ShowSavedConfirmationMessage(R.string.meal_updated)
            )
        }
    }

    fun onChangeTargetResult(result: Int) = viewModelScope.launch {
        when (result) {
            EDIT_TARGET_RESULT_OK -> eventChannel.send(
                Event.ShowSavedConfirmationMessage(R.string.target_updated)
            )
        }
    }

    sealed class Event {
        data class NavToAddMeal(val resId: Int) : Event()
        data class NavToMealContent(val title: String, val meal: Meal) : Event()
        data class NavToEditMeal(val resId: Int, val meal: Meal) : Event()
        data class NavToChangeTarget(val target: String) : Event()
        data class NavToDelete(val meal: Meal) : Event()
        data class ShowSavedConfirmationMessage(val resId: Int) : Event()
    }
}