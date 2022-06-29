package com.damian.myfitnessproject.ui.calculator.cardio

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
import damian.myfitnessproject.databinding.FragmentCalCardioBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CalCardioFragment : Fragment(R.layout.fragment_cal_cardio) {

    private val viewModel: CalCardioViewModel by viewModels()
    private lateinit var binding: FragmentCalCardioBinding

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

        binding = FragmentCalCardioBinding.bind(view)
        binding.apply {
            textViewSession.apply {
                setText(viewModel.sessions)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.sessions = text.toString()
                }
            }
            textViewDuration.apply {
                setText(viewModel.duration)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.duration = text.toString()
                }
            }
            radioLovCardio.apply {
                isSelected = viewModel.lov
                setOnClickListener { viewModel.onLovIntenseClick() }
            }
            radioMedCardio.apply {
                isSelected = viewModel.medium
                setOnClickListener { viewModel.onMediumIntenseClick() }
            }
            radioHighCardio.apply {
                isSelected = viewModel.high
                setOnClickListener { viewModel.onHighIntenseClick() }
            }

            btSkipCardio.setOnClickListener { viewModel.onSkipCardioClick() }
            btNextStep.setOnClickListener { viewModel.onNextStepClick() }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is CalCardioViewModel.Event.ShowSessionsError ->
                            binding.layoutSessions.error = getString(it.resId)
                        is CalCardioViewModel.Event.ShowDurationError ->
                            binding.layoutDuration.error = getString(it.resId)
                        is CalCardioViewModel.Event.ShowIntensityError ->
                            binding.tvIntensity.error = ""
                        is CalCardioViewModel.Event.NavToCalWeightLifting ->
                            findNavController().navigate(
                                CalCardioFragmentDirections
                                    .actionCalCardioFragmentToCalLiftingFragment()
                            )
                    }
                }
        }

        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}