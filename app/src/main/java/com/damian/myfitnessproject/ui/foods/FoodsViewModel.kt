package com.damian.myfitnessproject.ui.foods

import androidx.lifecycle.*
import androidx.navigation.fragment.FragmentNavigator
import com.damian.myfitnessproject.ADD_FOOD_RESULT_OK
import com.damian.myfitnessproject.EDIT_FOOD_RESULT_OK
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.repository.FoodRepoKt
import com.damian.myfitnessproject.data.repository.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodsViewModel @Inject constructor(
    private val foodRepoKt: FoodRepoKt,
    state: SavedStateHandle
) : ViewModel() {
    val searchQuery = state.getLiveData("searchQuery", "")

    private val foodFlow = searchQuery.asFlow().flatMapLatest {
        foodRepoKt.getAllItems(it)
    }
    val allFoodItems = foodFlow.asLiveData()

    fun onDeleteItemClick(foodItem: FoodItem) = viewModelScope.launch {
        eventChannel.send(Event.NavToDeleteItem(foodItem))
    }

    fun onAddItemClick() = viewModelScope
        .launch {
            eventChannel.send(Event.NavToAddItem("Add food"))
        }

    fun onEditItemClick(foodItem: FoodItem) = viewModelScope.launch {
        val wholeFood = foodRepoKt.getWholeFood(foodItem.id)
        eventChannel.send(Event.NavToEditItem(wholeFood))
    }

    fun onItemClick(
        foodItem: FoodItem,
        extras: FragmentNavigator.Extras
    ) = viewModelScope.launch {
        val wholeFood = foodRepoKt.getWholeFood(foodItem.id)
        val macroTitle = "Nutrition label (100 gm)"
        eventChannel.send(Event.NavToDetails(wholeFood, extras, macroTitle))
    }

    fun onAddEditResult(result: Int) = viewModelScope.launch {
        when (result) {
            ADD_FOOD_RESULT_OK -> eventChannel.send(
                Event.ShowMessage(R.string.meal_added)
            )
            EDIT_FOOD_RESULT_OK -> eventChannel.send(
                Event.ShowMessage(R.string.meal_updated)
            )
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowMessage(val resId: Int) : Event()
        data class NavToDeleteItem(val foodItem: FoodItem) : Event()
        data class NavToAddItem(val title: String) : Event()
        data class NavToEditItem(val wholeFood: Food) : Event()
        data class NavToDetails(
            val wholeFood: Food,
            val extras: FragmentNavigator.Extras,
            val macroTitle: String
        ) : Event()
    }
}