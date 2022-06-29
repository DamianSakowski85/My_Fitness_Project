package com.damian.myfitnessproject.ui.bodyMeasurements.lineCharts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.BodyMeasRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementsChartsViewModel @Inject constructor(
    private val bodyMeasRepo: BodyMeasRepo
) : ViewModel() {

    private val _bodyWeightData = MutableLiveData<List<Double>>()
    private val _waistDiameterData = MutableLiveData<List<Double>>()
    private val _midsectionDiameterData = MutableLiveData<List<Double>>()

    init {
        viewModelScope.launch {
            bodyMeasRepo.getAll().collect {
                val bodyWeightList = ArrayList<Double>()
                val waistDiameterList = ArrayList<Double>()
                val midsectionDiameterList = ArrayList<Double>()

                it.forEach { measurements ->
                    if (measurements.bodyWeight != 0.0) {
                        bodyWeightList.add(measurements.bodyWeight)
                    }
                    if (measurements.waistDiameter != 0.0) {
                        waistDiameterList.add(measurements.waistDiameter)
                    }
                    if (measurements.midsectionDiameter != 0.0) {
                        midsectionDiameterList.add(measurements.midsectionDiameter)
                    }
                }

                _bodyWeightData.value = bodyWeightList
                _waistDiameterData.value = waistDiameterList
                _midsectionDiameterData.value = midsectionDiameterList
            }
        }
    }

    val bodyWeightData: LiveData<List<Double>> = _bodyWeightData
    val waistDiameterData: LiveData<List<Double>> = _waistDiameterData
    val midsectionDiameterData: LiveData<List<Double>> = _midsectionDiameterData
}