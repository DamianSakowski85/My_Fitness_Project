package com.damian.myfitnessproject.ui.calculator.bmr

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentCalBmrBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CalBmrFragment : Fragment(R.layout.fragment_cal_bmr) {

    private val viewModel: CalBmrViewModel by viewModels()
    private lateinit var binding: FragmentCalBmrBinding

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

        binding = FragmentCalBmrBinding.bind(view)
        binding.apply {
            editTextBodyWeight.apply {
                setText(viewModel.bodyWeight)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.bodyWeight = text.toString()
                }
            }
            editTextHeight.apply {
                setText(viewModel.height)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.height = text.toString()
                }
            }
            editTextAge.apply {
                setText(viewModel.age)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.age = text.toString()
                }
            }
            radioMan.apply {
                isSelected = viewModel.man
                setOnClickListener { viewModel.onManClick() }
            }
            radioWoman.apply {
                isSelected = viewModel.woman
                setOnClickListener { viewModel.onWomanClick() }
            }

            btNextStep.setOnClickListener {
                viewModel.onNextStepClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is CalBmrViewModel.Event.ShowBodyWeightError ->
                            binding.textLayoutBodyWeight.error = getString(it.resId)
                        is CalBmrViewModel.Event.ShowHeightError ->
                            binding.textLayoutHeight.error = getString(it.resId)
                        is CalBmrViewModel.Event.ShowAgeError ->
                            binding.textLayoutAge.error = getString(it.resId)
                        is CalBmrViewModel.Event.ShowGenderError ->
                            binding.genderLabel.error = getString(it.resId)
                        is CalBmrViewModel.Event.NavToCalNeat -> {
                            val action = CalBmrFragmentDirections
                                .actionCalBmrFragmentToCalNeatFragment()
                            findNavController().navigate(action)
                        }

                    }
                }
        }
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}