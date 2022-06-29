package com.damian.myfitnessproject.ui.calculator.start

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.damian.myfitnessproject.data.CalculatorPreferences
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentCalStartBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalStartFragment : Fragment(R.layout.fragment_cal_start) {

    private val viewModel: CalStartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ true
        )
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val binding = FragmentCalStartBinding.bind(view)
        binding.apply {
            btStartCalculator.setOnClickListener {
                viewModel.onStartCalculatorClick()
            }
        }
        val pieChart = binding.layoutCalSummary.pieChart

        viewModel.currentCalResult.observe(viewLifecycleOwner) { calculator ->
            calculator?.let {
                binding.apply {
                    layoutCalSummary.apply {
                        textViewBmr.text =
                            getString(R.string.cal_format, calculator.bmr.toString())
                        textViewNeat.text =
                            getString(R.string.cal_format, calculator.neat.toString())
                        textViewCardio.text =
                            getString(R.string.cal_format, calculator.cardio.toString())
                        textViewWeightLifting.text =
                            getString(R.string.cal_format, calculator.weightLifting.toString())
                        textViewSummary.text =
                            getString(R.string.cal_format, calculator.summary.toString())
                    }
                }

                lifecycle.coroutineScope.launch {
                    pieChart.let {
                        setUpPieChart(it)
                        renderPieChart(it, calculator)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is CalStartViewModel.Event.NavToCalBmr -> {
                            findNavController().navigate(
                                CalStartFragmentDirections
                                    .actionCalStartFragmentToCalBmrFragment()
                            )
                        }
                    }
                }
        }

        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setUpPieChart(pieChart: PieChart) {
        pieChart.apply {
            isRotationEnabled = true
            holeRadius = 35f
            transparentCircleRadius = 45f
            description.isEnabled = true
            description.yOffset = -10f
            description.textSize = 10f
            legend.isEnabled = false
            setUsePercentValues(false)
            setCenterTextSize(14f)
            setHoleColor(Color.TRANSPARENT)
            setExtraOffsets(7f, 7f, 7f, 7f)
        }
    }

    private fun renderPieChart(pieChart: PieChart, calculatorPreferences: CalculatorPreferences) {

        val dataValues = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        val bmr = calculatorPreferences.bmr.toFloat()
        dataValues.add(PieEntry(bmr))
        colors.add(resources.getColor(R.color.fat, null))

        val neat = calculatorPreferences.neat.toFloat()
        dataValues.add(PieEntry(neat))
        colors.add(resources.getColor(R.color.carbohydrates, null))


        if (calculatorPreferences.cardio != 0) {
            val cardio = calculatorPreferences.cardio.toFloat()
            dataValues.add(PieEntry(cardio))
            colors.add(resources.getColor(R.color.protein, null))
        }

        if (calculatorPreferences.weightLifting != 0) {
            val weightLifting = calculatorPreferences.weightLifting.toFloat()
            dataValues.add(PieEntry(weightLifting))
            colors.add(resources.getColor(R.color.others, null))
        }
        //create the data set
        val pieDataSet = PieDataSet(dataValues, "")

        pieDataSet.apply {
            setDrawValues(true)
            sliceSpace = 0.2f
            valueTextSize = 10f
            this.colors = colors
            // Outside values
            valueLinePart1OffsetPercentage = 70f
            valueLinePart1Length = .6f
            valueLinePart2Length = .05f
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }
        val pieData = PieData(pieDataSet)

        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            pieChart.apply {
                setCenterTextColor(Color.WHITE)
                setEntryLabelColor(Color.WHITE)
                description.textColor = Color.WHITE
            }
            pieData.setValueTextColor(Color.WHITE)
            pieDataSet.valueLineColor = Color.WHITE
        }

        pieChart.apply {
            description.text = "Energy requirements"
            centerText = getString(R.string.cal_format, calculatorPreferences.summary.toString())
            data = pieData
            animateXY(600, 600)
        }
    }
}