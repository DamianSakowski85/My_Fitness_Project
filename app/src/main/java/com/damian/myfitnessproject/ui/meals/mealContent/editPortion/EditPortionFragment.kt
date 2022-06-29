package com.damian.myfitnessproject.ui.meals.mealContent.editPortion

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentEditPortionBinding
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class EditPortionFragment : Fragment(R.layout.fragment_edit_portion) {

    private val viewModel: EditPortionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val binding = FragmentEditPortionBinding.bind(view)

        binding.apply {
            textViewFoodName.text = viewModel.name
            textViewFoodDesc.text = viewModel.desc
            editTextPortion.apply {
                setText(viewModel.portionSize)
                requestFocus()
                showSoftKeyboard(this)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.portionSize = text.toString()
                }
            }
            fabSavePortion.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is EditPortionViewModel.Event.ShowPortionError ->
                            binding.tilPortion.error = getString(it.resId)
                        is EditPortionViewModel.Event.NavigateBackWithResult -> {
                            binding.editTextPortion.clearFocus()
                            setFragmentResult(
                                getString(R.string.edit_portion_request_key),
                                bundleOf(getString(R.string.result) to it.result)
                            )
                            findNavController().popBackStack()
                        }
                    }
                }
        }

        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}