package com.damian.myfitnessproject.ui.calculator.neat

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentCalNeatBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CalNeatFragment : Fragment(R.layout.fragment_cal_neat) {

    private val viewModel: CalNeatViewModel by viewModels()
    private lateinit var binding: FragmentCalNeatBinding

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

        binding = FragmentCalNeatBinding.bind(view)
        binding.apply {
            radioEktomorf.apply {
                isSelected = viewModel.ectomorph
                setOnClickListener { viewModel.onEctomorphClick() }
            }
            radioEndomorph.apply {
                isSelected = viewModel.endomorph
                setOnClickListener { viewModel.onEndomorphClick() }
            }
            radioMesomorph.apply {
                isSelected = viewModel.mesomorph
                setOnClickListener { viewModel.onMesomorphClick() }
            }
            radioLov.apply {
                isSelected = viewModel.lov
                setOnClickListener { viewModel.onLovActLevelClick() }
            }
            radioMed.apply {
                isSelected = viewModel.medium
                setOnClickListener { viewModel.onMedActLevelClick() }
            }
            radioHigh.apply {
                isSelected = viewModel.high
                setOnClickListener { viewModel.onHighActLevelClick() }
            }
            btNextStep.setOnClickListener {
                viewModel.onNextStepClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is CalNeatViewModel.Event.ShowBodyTypeError ->
                            binding.bodyTypeLabel.error = ""
                        is CalNeatViewModel.Event.ShowActivityError ->
                            binding.activityLabel.error = ""
                        is CalNeatViewModel.Event.NavToCalCardio ->
                            findNavController().navigate(
                                CalNeatFragmentDirections
                                    .actionCalNeatFragmentToCalCardioFragment()
                            )
                    }
                }
        }
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}