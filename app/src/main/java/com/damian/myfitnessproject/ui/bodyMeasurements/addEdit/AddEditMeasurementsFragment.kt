package com.damian.myfitnessproject.ui.bodyMeasurements.addEdit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentAddEditBodyMeasurementsBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditMeasurementsFragment : Fragment(R.layout.fragment_add_edit_body_measurements) {

    private val viewModel: AddEditMeasurementsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(
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

        val binding = FragmentAddEditBodyMeasurementsBinding.bind(view)
        binding.apply {
            textViewBodyWeight.apply {
                setText(viewModel.bodyWeight)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.bodyWeight = text.toString()
                }
            }

            textViewWaistDiameter.apply {
                setText(viewModel.waistDiameter)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.waistDiameter = text.toString()
                }
            }

            textViewMidsectionDiameter.apply {
                setText(viewModel.midSectionDiameter)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.midSectionDiameter = text.toString()
                }
            }

            fabSaveMeasurements.setOnClickListener {
                viewModel.onSaveButtonClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is AddEditMeasurementsViewModel.Event.ShowBodyWeightError ->
                            binding.layoutBodyWeight.error = resources.getString(it.resId)

                        is AddEditMeasurementsViewModel.Event.ShowWaistDiameterError ->
                            binding.layoutWaistDiameter.error = resources.getString(it.resId)

                        is AddEditMeasurementsViewModel.Event.ShowMidSecDiameterError ->
                            binding.layoutMidsectionDiameter.error = resources.getString(it.resId)

                        is AddEditMeasurementsViewModel.Event.NavBackWithResult -> {
                            setFragmentResult(
                                getString(R.string.add_edit_body_measurements_request_key),
                                bundleOf(getString(R.string.result) to it.result)
                            )
                            findNavController().popBackStack()
                        }
                    }
                }
        }
    }
}