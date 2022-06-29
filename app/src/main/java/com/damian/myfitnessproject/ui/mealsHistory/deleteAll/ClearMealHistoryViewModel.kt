package com.damian.myfitnessproject.ui.mealsHistory.deleteAll

import androidx.lifecycle.ViewModel
import com.damian.myfitnessproject.data.repository.MealsHistoryRepo
import com.damian.myfitnessproject.hilt.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClearMealHistoryViewModel @Inject constructor(
    private val mealsHistoryRepo: MealsHistoryRepo,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onPositiveButtonClick() = applicationScope.launch {
        mealsHistoryRepo.clearHistory()
    }
}