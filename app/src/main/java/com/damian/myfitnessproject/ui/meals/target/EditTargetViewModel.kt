package com.damian.myfitnessproject.ui.meals.target

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.EDIT_TARGET_RESULT_OK
import com.damian.myfitnessproject.data.repository.MealRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTargetViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mealRepo: MealRepo
) : ViewModel() {

    private var _targetValue: String? = null

    init {
        _targetValue = savedStateHandle.get<String>("target")
    }

    var target = savedStateHandle.get<String>("target") ?: _targetValue ?: ""
        set(value) {
            field = value
            savedStateHandle.set("target", value)
        }

    fun onSaveClick() = viewModelScope.launch {
        if (target.isBlank()) {
            eventChannel.send(Event.ShowTargetError(R.string.required_error))
        } else {
            kotlin.runCatching {
                mealRepo.updateTargetPref(target.toInt())
            }.onSuccess {
                eventChannel.send(Event.NavigateBackWithResult(EDIT_TARGET_RESULT_OK))
            }
        }
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowTargetError(val resId: Int) : Event()
        data class NavigateBackWithResult(val result: Int) : Event()
    }
}