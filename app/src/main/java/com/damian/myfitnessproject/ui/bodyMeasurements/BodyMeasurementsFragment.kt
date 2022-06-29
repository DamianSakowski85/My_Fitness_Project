package com.damian.myfitnessproject.ui.bodyMeasurements

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentBodyMeasurementsBinding
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class BodyMeasurementsFragment : Fragment(R.layout.fragment_body_measurements) {

    private val viewModel: BodyMeasurementsViewModel by viewModels()

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

        val binding = FragmentBodyMeasurementsBinding.bind(view)
        binding.apply {
            buttonOk.setOnClickListener {
                viewModel.onAddEditButtonClick()
            }
            buttonShowCharts.setOnClickListener {
                viewModel.onShowChartsButtonClick()
            }
        }

        viewModel.getCurrent.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    textViewBodyWeight.text = getString(R.string.kg_format, it.bodyWeightFormatted)
                    textViewWaistDiameter.text =
                        getString(R.string.cm_format, it.waistDiameterFormatted)
                    textViewMidsectionDiameter.text =
                        getString(R.string.cm_format, it.midsectionDiameterFormatted)
                }
            } ?: run {
                binding.apply {
                    textViewBodyWeight.text = ""
                    textViewWaistDiameter.text = ""
                    textViewMidsectionDiameter.text = ""
                }
            }
        }

        viewModel.getLast.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    textViewDate.text = it.dateFormatted
                    textViewLastBodyWeight.text =
                        getString(R.string.kg_format, it.bodyWeightFormatted)
                    textViewLastWaistDiameter.text =
                        getString(R.string.cm_format, it.waistDiameterFormatted)
                    textViewLastMidsectionDiameter.text =
                        getString(R.string.cm_format, it.midsectionDiameterFormatted)
                }
            } ?: run {
                binding.apply {
                    textViewDate.text = ""
                    textViewLastBodyWeight.text = ""
                    textViewLastWaistDiameter.text = ""
                    textViewLastMidsectionDiameter.text = ""
                }
            }
        }

        setFragmentResultListener(getString(R.string.add_edit_body_measurements_request_key)) { _, bundle ->
            val result = bundle.getInt(getString(R.string.result))
            viewModel.onAddEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is BodyMeasurementsViewModel.Event.NavToAdd -> findNavController().navigate(
                            BodyMeasurementsFragmentDirections
                                .actionNavBodyMeasurementsToAddEditMeasurementsFragment2()
                        )
                        is BodyMeasurementsViewModel.Event.NavToEdit -> {
                            val action = BodyMeasurementsFragmentDirections
                                .actionNavBodyMeasurementsToAddEditMeasurementsFragment2()
                            action.bodyMeasurements = it.bodyMeasurements
                            findNavController().navigate(action)
                        }
                        is BodyMeasurementsViewModel.Event.NavToDeleteAll -> findNavController()
                            .navigate(
                                BodyMeasurementsFragmentDirections
                                    .actionNavBodyMeasurementsToDeleteAllMeasurementsFragment()
                            )
                        is BodyMeasurementsViewModel.Event.NavToCharts -> findNavController()
                            .navigate(
                                BodyMeasurementsFragmentDirections
                                    .actionNavBodyMeasurementsToBodyMeasurementsChartsFragment2()
                            )
                        is BodyMeasurementsViewModel.Event.ShowMessage ->
                            Snackbar.make(view, getString(it.resId), Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_body_measurements, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_all) {
            viewModel.onDeleteAllClick()
        }
        return super.onOptionsItemSelected(item)
    }
}