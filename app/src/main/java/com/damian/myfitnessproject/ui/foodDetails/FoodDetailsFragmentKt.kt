package com.damian.myfitnessproject.ui.foodDetails

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.damian.myfitnessproject.data.database.entity.Food
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentFoodDetailsBinding
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class FoodDetailsFragmentKt : Fragment(R.layout.fragment_food_details) {

    private val viewModel: FoodDetailsViewModelKt by viewModels()

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
        postponeEnterTransition()

        val binding = FragmentFoodDetailsBinding.bind(view)

        val pieChart = binding.pieChart

        binding.apply {
            scrollView.transitionName = viewModel.transName
            textViewName.text = viewModel.name
            textViewDesc.text = viewModel.desc
            textViewMacroTitle.text = viewModel.title
        }

        viewModel.wholeFood.observe(viewLifecycleOwner) { food ->
            lifecycle.coroutineScope.launch {
                pieChart.let {
                    setUpPieChart(it)
                    renderPieChart(it, food,viewModel.portionSize)
                }
            }
        }

        viewModel.image.observe(viewLifecycleOwner) {
            binding.imageViewPhoto.setImageBitmap(it)
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
            minAngleForSlices = 5f
            setUsePercentValues(false)
            setCenterTextSize(14f)
            setHoleColor(Color.TRANSPARENT)
            setExtraOffsets(7f, 7f, 7f, 7f)
        }
    }


    private fun renderPieChart(pieChart: PieChart, food: Food,portionSize : Int) {
        val fatDouble = food.fats
        val carbsDouble = food.carbs
        val proteinsDouble = food.proteins

        val fat = fatDouble.toFloat()
        val carbs = carbsDouble.toFloat()
        val protein = proteinsDouble.toFloat()

        val dataValues = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        if (fatDouble != 0.0) {
            dataValues.add(PieEntry(fat))
            colors.add(resources.getColor(R.color.fat, null))
        }

        if (carbsDouble != 0.0) {
            dataValues.add(PieEntry(carbs))
            colors.add(resources.getColor(R.color.carbohydrates, null))
        }

        if (proteinsDouble != 0.0) {
            dataValues.add(PieEntry(protein))
            colors.add(resources.getColor(R.color.protein, null))
        }

        val others = portionSize - fat - carbs - protein
        dataValues.add(PieEntry(others))
        colors.add(resources.getColor(R.color.others, null))

        //create the data set
        val pieDataSet = PieDataSet(dataValues, "")

        pieDataSet.valueFormatter = DefaultValueFormatter(1)
        //where digits is the number of decimal places

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
            description.text = "Macronutrients (gm)"
            centerText = getString(R.string.cal_format, food.cal.toString())
            data = pieData
            animateXY(600, 600)
        }
    }
}