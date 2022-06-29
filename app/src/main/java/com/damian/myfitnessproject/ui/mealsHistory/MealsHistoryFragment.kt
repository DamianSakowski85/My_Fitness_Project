package com.damian.myfitnessproject.ui.mealsHistory

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damian.myfitnessproject.calendar.CustomCalendarValidatorKt
import com.damian.myfitnessproject.data.database.entity.Meal
import com.damian.myfitnessproject.data.repository.model.CalendarConstraintsModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentMealsHistoryBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class MealsHistoryFragment : Fragment(R.layout.fragment_meals_history),
    MealHistoryAdapter.OnItemClickListener {

    private val viewModel: MealsHistoryViewModel by viewModels()
    private lateinit var dataPicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        val binding = FragmentMealsHistoryBinding.bind(view)
        val adapter = MealHistoryAdapter(this)

        binding.apply {
            rvMealsHistory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                this.adapter = adapter
            }

            btPickDate.setOnClickListener {
                viewModel.onDatePickerShowClick()
            }
        }

        viewModel.calendarConstraints.observe(viewLifecycleOwner) {
            setupDataPicker(it)
        }

        viewModel.allMeals.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            when {
                it.isEmpty() -> {
                    binding.tvEmptyList.visibility = View.VISIBLE
                    binding.btPickDate.visibility = View.GONE
                }
                it.isNotEmpty() -> {
                    binding.tvEmptyList.visibility = View.GONE
                    binding.btPickDate.visibility = View.VISIBLE
                }
            }
        }

        viewModel.summary.observe(viewLifecycleOwner) {
            binding.mealsSummary.apply {
                textViewIntake.text = it.calFormatted
                textViewFats.text = it.fatsFormatted
                textViewCarbs.text = it.carbsFormatted
                textViewProteins.text = it.proteinsFormatted
            }
        }

        viewModel.title.observe(viewLifecycleOwner) {
            binding.title.text = it
        }

        viewModel.eventsFlow
            .onEach {
                when (it) {
                    is MealsHistoryViewModel.Event.ShowDatePicker ->
                        dataPicker.show(
                            requireActivity().supportFragmentManager,
                            dataPicker.toString()
                        )
                    is MealsHistoryViewModel.Event.NavToDetails -> {
                        val action = MealsHistoryFragmentDirections
                            .actionNavMealsHistoryToMealsHistoryContentFragment(it.meal)
                        findNavController().navigate(action)
                    }
                    is MealsHistoryViewModel.Event.NavToDeleteAll ->
                        findNavController().navigate(
                            MealsHistoryFragmentDirections
                                .actionNavMealsHistoryToClearMealHistoryDialog()
                        )
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun setupDataPicker(calendarConstraintsData: CalendarConstraintsModel) {

        val calendarConstraints = getCalendarConstraints(calendarConstraintsData)

        val startDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val parsedDate = LocalDate.parse(calendarConstraintsData.previousDate)

        val year = parsedDate.year
        val month = parsedDate.monthValue - 1
        val day = parsedDate.dayOfMonth
        startDate.set(year, month, day)

        dataPicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Previous meals")
            .setCalendarConstraints(calendarConstraints)
            .setSelection(startDate.timeInMillis)
            .build()


        dataPicker.addOnPositiveButtonClickListener { selection ->
            val triggerTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(selection),
                TimeZone.getDefault().toZoneId()
            )
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            viewModel.onPickDate(triggerTime.format(formatter))
        }
    }

    private fun getCalendarConstraints(constraints: CalendarConstraintsModel): CalendarConstraints {
        return CalendarConstraints.Builder()
            .setStart(constraints.startDate.timeInMillis)
            .setEnd(constraints.endDate.timeInMillis)
            .setValidator(
                CustomCalendarValidatorKt(
                    constraints.dates
                )
            )
            .build()
    }

    override fun onItemClick(meal: Meal, view: View) {
        viewModel.onItemClick(meal)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_meals_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_all) {
            viewModel.onDeleteAllClick()
        }
        return super.onOptionsItemSelected(item)
    }
}