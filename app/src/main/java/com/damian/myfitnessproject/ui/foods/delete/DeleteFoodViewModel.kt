package com.damian.myfitnessproject.ui.foods.delete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.damian.myfitnessproject.data.repository.FoodRepoKt
import com.damian.myfitnessproject.data.repository.model.FoodItem
import com.damian.myfitnessproject.hilt.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteFoodViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val foodRepoKt: FoodRepoKt,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private var _foodItem: FoodItem? = savedStateHandle.get<FoodItem>("foodItem")

    val message = "Delete ${_foodItem?.name} ?"

    fun onPositiveButtonClick() = applicationScope.launch {
        _foodItem?.let {
            foodRepoKt.delete(it)
        }
    }
}