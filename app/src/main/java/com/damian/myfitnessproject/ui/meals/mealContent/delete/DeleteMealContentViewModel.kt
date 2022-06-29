package com.damian.myfitnessproject.ui.meals.mealContent.delete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.damian.myfitnessproject.data.repository.MealContentRepo
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import com.damian.myfitnessproject.hilt.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteMealContentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealContentRepo: MealContentRepo,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private var mealContentItem: MealContentItem? =
        savedStateHandle.get<MealContentItem>("mealContentItem")

    val message = "Delete ${mealContentItem?.name} ?"

    fun onPositiveButtonClick() = applicationScope.launch {
        mealContentItem?.let {
            mealContentRepo.delete(it.portion)
        }
    }
}