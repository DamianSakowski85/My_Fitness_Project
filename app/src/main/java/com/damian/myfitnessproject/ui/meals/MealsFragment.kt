package com.damian.myfitnessproject.ui.meals

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damian.myfitnessproject.data.database.entity.Meal
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentMealsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MealsFragment : Fragment(R.layout.fragment_meals), MealAdapter.OnItemClickListener {

    private val viewModel: MealsViewModel by viewModels()

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

        val binding = FragmentMealsBinding.bind(view)
        val adapter = MealAdapter(this)

        binding.apply {
            rvMeals.apply {
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                this.adapter = adapter
                fabAddMeal.setOnClickListener {
                    viewModel.onAddMealClick()
                }
                textViewChangeTarget.setOnClickListener {
                    viewModel.onChangeTargetClick()
                }
            }
        }

        viewModel.currentMeals.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.tvEmptyList.visibility = View.VISIBLE
            } else {
                binding.tvEmptyList.visibility = View.GONE
            }
        }

        viewModel.summary.observe(viewLifecycleOwner) {
            binding.apply {
                textViewIntake.text = it.calFormatted
                textViewTarget.text = it.targetFormatted
                textViewLeft.text = it.leftFormatted
                textViewFats.text = it.fatsFormatted
                textViewCarbs.text = it.carbsFormatted
                textViewProteins.text = it.proteinsFormatted

                progressLinear.min = 0
                progressLinear.max = it.target
                progressLinear.progress = it.cal
            }
        }

        setFragmentResultListener(getString(R.string.add_edit_meal_request_key)) { _, bundle ->
            val result = bundle.getInt(getString(R.string.result))
            viewModel.onAddEditResult(result)
        }

        setFragmentResultListener(getString(R.string.edit_target_request_key)) { _, bundle ->
            val result = bundle.getInt(getString(R.string.result))
            viewModel.onChangeTargetResult(result)
        }

        viewModel.eventsFlow
            .onEach {
                when (it) {
                    is MealsViewModel.Event.NavToDelete -> {
                        val action = MealsFragmentDirections
                            .actionNavMealsToDeleteMealDialog(it.meal)
                        findNavController().navigate(action)
                    }
                    is MealsViewModel.Event.NavToAddMeal -> navToAddMeal(it.resId)

                    is MealsViewModel.Event.NavToEditMeal -> {
                        val action = MealsFragmentDirections
                            .actionNavMealsToAddEditMealFragment(getString(it.resId))
                        action.meal = it.meal
                        findNavController().navigate(action)
                    }
                    is MealsViewModel.Event.NavToChangeTarget -> {
                        val action = MealsFragmentDirections
                            .actionNavMealsToEditTargetFragment(it.target)
                        findNavController().navigate(action)
                    }
                    is MealsViewModel.Event.NavToMealContent -> {
                        val action = MealsFragmentDirections
                            .actionNavMealsToMealContentFragment(it.title, it.meal)
                        findNavController().navigate(action)
                    }
                    is MealsViewModel.Event.ShowSavedConfirmationMessage -> {
                        Toast.makeText(
                            requireContext(),
                            getString(it.resId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onItemClick(meal: Meal, view: View) {
        viewModel.onMealClick(meal)
    }

    override fun onMenuItemClick(meal: Meal, menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_change_name -> {
                viewModel.onChangeNameClick(meal)
            }
            R.id.action_remove -> {
                viewModel.onDeleteItemClick(meal)
            }
        }
    }

    private fun navToAddMeal(resId: Int) {
        val action = MealsFragmentDirections
            .actionNavMealsToAddEditMealFragment(getString(resId))

        findNavController().navigate(action)
    }
}