package com.damian.myfitnessproject.ui.calculator.lifting

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
import damian.myfitnessproject.databinding.FragmentCalLiftingBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CalLiftingFragment : Fragment(R.layout.fragment_cal_lifting) {

    private val viewModel: CalLiftingViewModel by viewModels()
    private lateinit var binding: FragmentCalLiftingBinding

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

        binding = FragmentCalLiftingBinding.bind(view)
        binding.apply {
            textViewSessions.apply {
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
            radioLovLifting.apply {
                isSelected = viewModel.lov
                setOnClickListener { viewModel.onLovIntenseClick() }
            }
            radioMedLifting.apply {
                isSelected = viewModel.medium
                setOnClickListener { viewModel.onMediumIntenseClick() }
            }
            radioHighLifting.apply {
                isSelected = viewModel.high
                setOnClickListener { viewModel.onHighIntenseClick() }
            }

            btSkipLifting.setOnClickListener { viewModel.onSkipWeightLiftingClick() }
            btNextStep.setOnClickListener { viewModel.onNextStepClick() }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is CalLiftingViewModel.Event.ShowSessionsError ->
                            binding.layoutSession.error = getString(it.resId)
                        is CalLiftingViewModel.Event.ShowDurationError ->
                            binding.layoutDuration.error = getString(it.resId)
                        is CalLiftingViewModel.Event.ShowIntensityError ->
                            binding.intensity.error = ""
                        is CalLiftingViewModel.Event.NavToSummary ->
                            findNavController().navigate(
                                CalLiftingFragmentDirections
                                    .actionCalLiftingFragmentToCalSummaryFragment()
                            )
                    }
                }
        }

        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}