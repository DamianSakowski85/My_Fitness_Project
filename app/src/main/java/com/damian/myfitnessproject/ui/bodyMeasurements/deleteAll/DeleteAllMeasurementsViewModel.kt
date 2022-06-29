package com.damian.myfitnessproject.ui.bodyMeasurements.deleteAll

import androidx.lifecycle.ViewModel
import com.damian.myfitnessproject.data.repository.BodyMeasRepo
import com.damian.myfitnessproject.hilt.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllMeasurementsViewModel @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val bodyMeasRepo: BodyMeasRepo
) : ViewModel() {

    fun onPositiveButtonClick() = applicationScope.launch {
        bodyMeasRepo.deleteAll()
    }
}