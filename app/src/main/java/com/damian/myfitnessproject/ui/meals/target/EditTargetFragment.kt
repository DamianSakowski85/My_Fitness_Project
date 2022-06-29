package com.damian.myfitnessproject.ui.meals.target

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
import damian.myfitnessproject.databinding.FragmentEditTargetBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EditTargetFragment : Fragment(R.layout.fragment_edit_target) {

    private val viewModel: EditTargetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
            /* forward= */ true
        )
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
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

        val binding = FragmentEditTargetBinding.bind(view)

        binding.apply {
            editTextTarget.apply {
                setText(viewModel.target)
                requestFocus()
                showSoftKeyboard(this)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.target = text.toString()
                }
            }
            fabSaveTarget.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is EditTargetViewModel.Event.ShowTargetError ->
                            binding.tilTarget.error = getString(it.resId)
                        is EditTargetViewModel.Event.NavigateBackWithResult -> {
                            binding.editTextTarget.clearFocus()
                            setFragmentResult(
                                getString(R.string.edit_target_request_key),
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