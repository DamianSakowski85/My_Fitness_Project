package com.damian.myfitnessproject.ui.meals.mealContent

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentMealContentBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MealContentFragment : Fragment(R.layout.fragment_meal_content),
    MealContentAdapter.OnItemClickListener {

    private val viewModel: MealContentViewModel by viewModels()

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
        postponeEnterTransition()

        val binding = FragmentMealContentBinding.bind(view)
        val adapter = MealContentAdapter(this)

        binding.apply {
            rvMealContent.layoutManager = LinearLayoutManager(requireContext())
            rvMealContent.hasFixedSize()
            rvMealContent.adapter = adapter
            fabSelectContent.setOnClickListener {
                viewModel.onSelectContentClick()
            }
        }

        viewModel.allData?.observe(viewLifecycleOwner) {
            adapter.submitList(it.foods)
            if (it.foods.isEmpty()) {
                binding.textViewEmptyList.visibility = View.VISIBLE
            } else {
                binding.textViewEmptyList.visibility = View.GONE
            }

            binding.apply {
                mealsSummary.apply {
                    textViewIntake.text = it.contentSummary.calFormatted
                    textViewFats.text = it.contentSummary.fatsFormatted
                    textViewCarbs.text = it.contentSummary.carbsFormatted
                    textViewProteins.text = it.contentSummary.proteinsFormatted
                }
            }
        }

        viewModel.eventsFlow
            .onEach {
                when (it) {
                    is MealContentViewModel.Event.NavToDelete -> {
                        val action = MealContentFragmentDirections
                            .actionMealContentFragmentToDeleteMealContentDialog(it.mealContentItem)
                        findNavController().navigate(action)
                    }
                    is MealContentViewModel.Event.NavToChangePortion -> {
                        val action = MealContentFragmentDirections
                            .actionMealContentFragmentToEditPortionFragment(it.mealContentItem)
                        findNavController().navigate(action)
                    }
                    is MealContentViewModel.Event.NavToContentDetails -> {
                        val action = MealContentFragmentDirections
                            .actionGlobalNavFoodDetails(it.food, it.title,it.portionSize)
                        findNavController().navigate(action)
                    }
                    is MealContentViewModel.Event.NavToSelectContent -> {
                        val action = MealContentFragmentDirections
                            .actionMealContentFragmentToSelectFoodsFragment(it.title, it.meal)
                        findNavController().navigate(action)
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onItemClick(mealContentItem: MealContentItem, view: View) {
        viewModel.onContentItemClick(mealContentItem)
    }

    override fun onMenuItemClick(mealContentItem: MealContentItem, menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_change_portion -> {
                viewModel.onChangePortionClick(mealContentItem)
            }
            R.id.action_remove -> {
                viewModel.onDeletePortionClick(mealContentItem)
            }
        }
    }
}