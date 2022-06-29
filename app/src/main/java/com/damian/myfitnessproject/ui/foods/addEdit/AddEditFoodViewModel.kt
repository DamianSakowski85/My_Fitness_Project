package com.damian.myfitnessproject.ui.foods.addEdit

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.damian.myfitnessproject.ADD_FOOD_RESULT_OK
import com.damian.myfitnessproject.EDIT_FOOD_RESULT_OK
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.repository.FoodRepoKt
import dagger.hilt.android.lifecycle.HiltViewModel
import damian.myfitnessproject.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditFoodViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val foodRepoKt: FoodRepoKt,
) : ViewModel() {

    private var _foodToEdit: Food? = null
    private val _bitmap = MutableLiveData<Bitmap>()

    init {
        _foodToEdit = state.get<Food>("food")
    }

    val image: LiveData<Bitmap> = _bitmap

    var name = state.get<String>("foodName") ?: _foodToEdit?.name ?: ""
        set(value) {
            field = value
            state.set("foodName", value)
        }

    var description = state.get<String>("foodDesc") ?: _foodToEdit?.description ?: ""
        set(value) {
            field = value
            state.set("foodDesc", value)
        }

    var cal = state.get<String>("cal") ?: _foodToEdit?.cal?.toString() ?: ""
        set(value) {
            field = value
            state.set("cal", value)
        }

    var fats = state.get<String>("fats") ?: _foodToEdit?.fats?.toString() ?: ""
        set(value) {
            field = value
            state.set("fats", value)
        }

    var carbs = state.get<String>("carbs") ?: _foodToEdit?.carbs?.toString() ?: ""
        set(value) {
            field = value
            state.set("carbs", value)
        }

    var proteins = state.get<String>("proteins") ?: _foodToEdit?.proteins?.toString() ?: ""
        set(value) {
            field = value
            state.set("proteins", value)
        }

    fun onSaveClick() = viewModelScope.launch {
        if (nameIsCorrect() && calIsCorrect() && fatsIsCorrect() && carbsIsCorrect() && proteinsIsCorrect()) {
            val calValue = cal.toInt()
            val fatsValue = fats.toDouble()
            val carbsValue = carbs.toDouble()
            val proteinsValue = proteins.toDouble()

            if (marcoIsCorrect(fatsValue, carbsValue, proteinsValue)) {
                _foodToEdit?.let {
                    kotlin.runCatching {
                        val food = it.copy(
                            name = name,
                            description = description,
                            cal = calValue,
                            fats = fatsValue,
                            carbs = carbsValue,
                            proteins = proteinsValue
                        )
                        _bitmap.value?.let {
                            foodRepoKt.update(food, it)
                        } ?: run {
                            foodRepoKt.update(food)
                        }
                    }.onSuccess {
                        eventChannel.send(Event.NavBackWithResult(EDIT_FOOD_RESULT_OK))
                    }

                } ?: run {
                    kotlin.runCatching {
                        val food = Food(
                            name,
                            description,
                            calValue,
                            fatsValue,
                            carbsValue,
                            proteinsValue
                        )
                        _bitmap.value?.let {
                            foodRepoKt.insert(food, it)
                        } ?: run {
                            foodRepoKt.insert(food)
                        }
                    }.onSuccess {
                        eventChannel.send(Event.NavBackWithResult(ADD_FOOD_RESULT_OK))
                    }
                }
            } else {
                eventChannel.send(
                    Event
                        .ShowMacroError(R.string.macros_not_correct)
                )
            }
        }
    }

    fun onTakeImageClick() = viewModelScope.launch {
        eventChannel.send(Event.OpenPhotoDialog)
    }

    fun onByteArrayResult(byteArray: ByteArray?) = viewModelScope.launch {
        byteArray?.let {
            val bitmap = foodRepoKt.convertByteArrayToBitmap(it)
            _bitmap.value = bitmap
        }
    }

    private suspend fun nameIsCorrect(): Boolean {
        return when {
            name.isBlank() -> {
                eventChannel.send(Event.ShowNameError(R.string.required_error))
                false
            }
            else -> {
                eventChannel.send(Event.ShowNameError(R.string.empty))
                true
            }
        }
    }

    private suspend fun calIsCorrect(): Boolean {
        return when {
            cal.isBlank() -> {
                eventChannel.send(Event.ShowCalError(R.string.required_error))
                false
            }
            else -> {
                eventChannel.send(Event.ShowCalError(R.string.empty))
                true
            }
        }
    }

    private suspend fun fatsIsCorrect(): Boolean {
        return when {
            fats.isBlank() -> {
                eventChannel.send(Event.ShowFatsError(R.string.required_error))
                false
            }
            containsToManyPoints(fats) -> {
                eventChannel.send(Event.ShowFatsError(R.string.to_many_points))
                false
            }
            else -> {
                eventChannel.send(Event.ShowFatsError(R.string.empty))
                true
            }
        }
    }

    private suspend fun carbsIsCorrect(): Boolean {
        return when {
            carbs.isBlank() -> {
                eventChannel.send(Event.ShowCarbsError(R.string.required_error))
                false
            }
            containsToManyPoints(carbs) -> {
                eventChannel.send(Event.ShowCarbsError(R.string.to_many_points))
                false
            }
            else -> {
                eventChannel.send(Event.ShowCarbsError(R.string.empty))
                true
            }
        }
    }

    private suspend fun proteinsIsCorrect(): Boolean {
        return when {
            proteins.isBlank() -> {
                eventChannel.send(Event.ShowProteinsError(R.string.required_error))
                false
            }
            containsToManyPoints(proteins) -> {
                eventChannel.send(Event.ShowProteinsError(R.string.to_many_points))
                false
            }
            else -> {
                eventChannel.send(Event.ShowProteinsError(R.string.empty))
                true
            }
        }
    }

    private fun containsToManyPoints(value: String): Boolean {
        val chars = value.toCharArray()
        var contains = false
        chars.forEach {
            if (it == '.') {
                if (contains) {
                    return true
                }
                contains = true
            }
        }
        return false
    }

    private fun marcoIsCorrect(fats: Double, carbs: Double, proteins: Double): Boolean {
        val summary = (fats + carbs + proteins)
        return summary <= 100
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    sealed class Event {
        data class ShowNameError(val resId: Int) : Event()
        data class ShowCalError(val resId: Int) : Event()
        data class ShowFatsError(val resId: Int) : Event()
        data class ShowCarbsError(val resId: Int) : Event()
        data class ShowProteinsError(val resId: Int) : Event()
        data class ShowMacroError(val resId: Int) : Event()
        data class NavBackWithResult(val result: Int) : Event()
        object OpenPhotoDialog : Event()
    }
}