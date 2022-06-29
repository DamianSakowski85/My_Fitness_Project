package com.damian.myfitnessproject.ui.meals.mealContent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.MealContentRepo
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealContentViewModel @Inject constructor(
    private val mealContentRepo: MealContentRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private var _meal: Meal? = savedStateHandle.get<Meal>("meal")

    val allData = _meal?.let {
        mealContentRepo.allForMeal(it.id).map { portions ->
            mealContentRepo.collectData(portions)
        }
    }?.asLiveData()

    fun onContentItemClick(mealContentItem: MealContentItem) = viewModelScope.launch {
        val food = mealContentRepo.calculateMacroForPortion(mealContentItem)
        eventChannel.send(
            Event.NavToContentDetails(
                "Macronutrients in your portion (${mealContentItem.portion.size} gm)",
                food,
                mealContentItem.portion.size
            )
        )
    }

    fun onChangePortionClick(mealContentItem: MealContentItem) = viewModelScope.launch {
        eventChannel.send(Event.NavToChangePortion(mealContentItem))
    }

    fun onDeletePortionClick(mealContentItem: MealContentItem) = viewModelScope.launch {
        eventChannel.send(Event.NavToDelete(mealContentItem))
    }

    fun onSelectContentClick() = viewModelScope.launch {
        _meal?.let {
            eventChannel.send(Event.NavToSelectContent("Foods for ${it.name}", it))
        }
    }

    sealed class Event {
        data class NavToSelectContent(val title: String, val meal: Meal) : Event()
        data class NavToContentDetails(val title: String, val food: Food, val portionSize : Int) : Event()
        data class NavToChangePortion(val mealContentItem: MealContentItem) : Event()
        data class NavToDelete(val mealContentItem: MealContentItem) : Event()
    }
}