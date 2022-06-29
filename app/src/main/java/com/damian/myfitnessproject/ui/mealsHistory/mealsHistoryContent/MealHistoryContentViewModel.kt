package com.damian.myfitnessproject.ui.mealsHistory.mealsHistoryContent

import androidx.lifecycle.*
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.MealContentRepo
import com.damian.myfitnessproject.data.repository.MealsHistoryRepo
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealHistoryContentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealsHistoryRepo: MealsHistoryRepo,
    private val mealContentRepo: MealContentRepo
) : ViewModel() {

    private val _meal = savedStateHandle.getLiveData<Meal>("meal")
    private val dataFlow = _meal.asFlow().flatMapLatest {
        mealsHistoryRepo.mealSummary(it)
    }

    val allData = dataFlow.asLiveData()
    val meal: LiveData<Meal> = _meal

    fun onItemClick(mealContentItem: MealContentItem) = viewModelScope.launch {
        val food = mealContentRepo.calculateMacroForPortion(mealContentItem)
        eventChannel.send(
            Event.NavToDetails(
                "Macronutrients in your portion (${mealContentItem.portion.size} gm)",
                food,
                mealContentItem.portion.size
            )
        )
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class NavToDetails(val title: String, val food: Food, val portionSize: Int) : Event()
    }
}