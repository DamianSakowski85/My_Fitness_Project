package com.damian.myfitnessproject.ui.mealsHistory.mealsHistoryContent

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentMealHistoryContentBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MealHistoryContentFragment : Fragment(R.layout.fragment_meal_history_content),
    MealHistoryContentAdapter.OnItemClickListener {

    private val viewModel: MealHistoryContentViewModel by viewModels()

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

        val binding = FragmentMealHistoryContentBinding.bind(view)
        val adapter = MealHistoryContentAdapter(this)

        binding.apply {
            rvContent.apply {
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                this.adapter = adapter
            }
        }
        viewModel.meal.observe(viewLifecycleOwner) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                "History (${it.name})"
            binding.textViewTitle.text = it.dateFormatted
        }

        viewModel.allData.observe(viewLifecycleOwner) {
            adapter.submitList(it.foods)
            binding.layoutSummary.apply {
                textViewIntake.text = it.contentSummary.calFormatted
                textViewFats.text = it.contentSummary.fatsFormatted
                textViewCarbs.text = it.contentSummary.carbsFormatted
                textViewProteins.text = it.contentSummary.proteinsFormatted
            }

        }

        viewModel.eventsFlow
            .onEach {
                when (it) {
                    is MealHistoryContentViewModel.Event.NavToDetails -> {
                        val action = MealHistoryContentFragmentDirections
                            .actionGlobalNavFoodDetails(it.food, it.title,it.portionSize)
                        findNavController().navigate(action)
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onItemClick(mealContentItem: MealContentItem, view: View) {
        viewModel.onItemClick(mealContentItem)
    }
}