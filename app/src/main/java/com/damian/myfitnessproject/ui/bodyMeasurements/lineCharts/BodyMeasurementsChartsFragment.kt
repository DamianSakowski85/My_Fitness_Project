package com.damian.myfitnessproject.ui.bodyMeasurements.lineCharts

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentBodyMeasurementsChartsBinding

@AndroidEntryPoint
class BodyMeasurementsChartsFragment : Fragment(R.layout.fragment_body_measurements_charts) {

    private val viewModel: BodyMeasurementsChartsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ true
        )
        returnTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBodyMeasurementsChartsBinding.bind(view)

        viewModel.bodyWeightData.observe(viewLifecycleOwner) {
            if (it.size > 4) {
                renderChartData(binding.bodyWeightChart, "Body weight (kg)", it)
            }
        }

        viewModel.waistDiameterData.observe(viewLifecycleOwner) {
            if (it.size > 4) {
                renderChartData(binding.waistChart, "Waist diameter (cm)", it)
            }
        }

        viewModel.midsectionDiameterData.observe(viewLifecycleOwner) {
            if (it.size > 4) {
                renderChartData(binding.midsectionChart, "Midsection diameter (cm)", it)
            }
        }
    }

    private fun renderChartData(lineChart: LineChart, label: String, measurement: List<Double>) {
        val lineEntries = ArrayList<Entry>()

        var index = 1
        measurement.forEach {
            lineEntries.add(Entry(index.toFloat(), it.toFloat()))
            index++
        }

        lineChart.apply {
            axisRight.isEnabled = false
            axisLeft.isEnabled = true
            description.text = "Days on chart"

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                isGranularityEnabled = true
                granularity = 1f
            }
        }

        val lineDataSet = LineDataSet(lineEntries, label)
        lineDataSet.apply {
            lineWidth = 3f
            color = resources.getColor(R.color.teal_700, null)
            setDrawValues(false)
            setDrawFilled(true)
            setDrawCircles(false)
        }

        val lineData = LineData(lineDataSet)

        lineChart.data = lineData

        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            lineDataSet.valueTextColor = Color.WHITE
            lineChart.apply {
                xAxis.textColor = Color.WHITE
                axisLeft.textColor = Color.WHITE
                description.textColor = Color.WHITE
                legend.textColor = Color.WHITE
            }
        }

        lineChart.apply {
            setVisibleXRangeMaximum(20f)
            setVisibleXRangeMinimum(3f)
            moveViewToX(measurement.size.toFloat())
            animateXY(600, 600)
        }
    }
}