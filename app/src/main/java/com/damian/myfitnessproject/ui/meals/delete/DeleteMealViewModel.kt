package com.damian.myfitnessproject.ui.meals.delete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.MealRepo
import com.damian.myfitnessproject.hilt.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteMealViewModel @Inject constructor(
    private val mealRepo: MealRepo,
    savedStateHandle: SavedStateHandle,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private var _meal: Meal? = savedStateHandle.get<Meal>("meal")

    val message = "Delete ${_meal?.name} ?"

    fun onPositiveButtonClick() = applicationScope.launch {
        _meal?.let {
            mealRepo.delete(it)
        }
    }
}