package com.damian.myfitnessproject.ui.meals.mealContent.selectFoods.addPortion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.ADD_PORTION_RESULT_OK
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.database.entity.Portion
import com.damian.myfitnessproject.data.repository.SelectContentRepo
import com.damian.myfitnessproject.data.repository.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddPortionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val selectContentRepo: SelectContentRepo
) : ViewModel() {

    private val _meal: Meal? = savedStateHandle.get<Meal>("meal")
    private val _foodItem: FoodItem? = savedStateHandle.get<FoodItem>("foodItem")

    val name = _foodItem?.name
    val desc = _foodItem?.description


    var portionSize = savedStateHandle.get<String>("portionSize") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("portionSize", value)
        }

    fun onSaveClick() = viewModelScope.launch {
        if (portionSize.isBlank()) {
            eventChannel.send(Event.ShowPortionError(R.string.required_error))
        } else {
            kotlin.runCatching {
                if (_foodItem != null && _meal != null) {
                    val portion = Portion(
                        _foodItem.id, _meal.id,
                        LocalDate.now().toString(),
                        portionSize.toInt()
                    )
                    selectContentRepo.insert(portion)
                }
            }.onSuccess {
                eventChannel.send(
                    Event.NavigateBackWithResult(
                        ADD_PORTION_RESULT_OK
                    )
                )
            }
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowPortionError(val resId: Int) : Event()
        data class NavigateBackWithResult(val result: Int) : Event()
    }
}