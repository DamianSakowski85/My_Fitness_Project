package com.damian.myfitnessproject.ui.meals.mealContent.selectFoods

import androidx.lifecycle.*
import com.damian.myfitnessproject.ADD_PORTION_RESULT_OK
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.SelectContentRepo
import com.damian.myfitnessproject.data.repository.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectFoodViewModel @Inject constructor(
    private val selectContentRepo: SelectContentRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _meal = savedStateHandle.get<Meal>("meal")

    val searchQuery = savedStateHandle.getLiveData("searchQuery", "")

    lateinit var nonSelectedFoods: LiveData<List<FoodItem>>

    init {
        _meal?.let { meal ->
            nonSelectedFoods = searchQuery.asFlow().flatMapLatest {
                selectContentRepo.test(meal.id, it)
            }.asLiveData()
        }
    }

    fun onItemClick(foodItem: FoodItem) = viewModelScope.launch {
        _meal?.let {
            eventChannel.send(Event.NavToAddPortion(it.name, it, foodItem))
        }
    }

    fun onAddPortionResult(result: Int) = viewModelScope.launch {
        when (result) {
            ADD_PORTION_RESULT_OK -> {
                eventChannel.send(Event.ShowMessage(R.string.selected_food_added_to_meal_list))
            }
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class NavToAddPortion(val title: String, val meal: Meal, val foodItem: FoodItem) :
            Event()

        data class ShowMessage(val resId: Int) : Event()
    }
}