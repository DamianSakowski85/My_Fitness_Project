package com.damian.myfitnessproject.ui.meals.mealContent.editPortion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.EDIT_TARGET_RESULT_OK
import com.damian.myfitnessproject.data.repository.MealContentRepo
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPortionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mealContentRepo: MealContentRepo
) : ViewModel() {

    private var _mealContentItem: MealContentItem? = null

    init {
        _mealContentItem = savedStateHandle.get<MealContentItem>("mealContentItem")
    }

    val name = _mealContentItem?.name
    val desc = _mealContentItem?.description

    var portionSize =
        savedStateHandle.get<String>("portionSize") ?: _mealContentItem?.portion?.size.toString()
        set(value) {
            field = value
            savedStateHandle.set("portionSize", value)
        }

    fun onSaveClick() = viewModelScope.launch {
        if (portionSize.isBlank()) {
            eventChannel.send(Event.ShowPortionError(R.string.required_error))
        } else {
            kotlin.runCatching {
                _mealContentItem?.let {
                    val portion = it.portion.copy(
                        size = portionSize.toInt()
                    )
                    mealContentRepo.updatePortion(portion)
                }

            }.onSuccess {
                eventChannel.send(Event.NavigateBackWithResult(EDIT_TARGET_RESULT_OK))
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