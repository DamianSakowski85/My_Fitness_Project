package com.damian.myfitnessproject.ui.meals.addEdit

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
import damian.myfitnessproject.databinding.FragmentMealAddEditBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditMealFragment : Fragment(R.layout.fragment_meal_add_edit) {

    private val viewModel: AddEditMealViewModel by viewModels()

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
        view.doOnPreDraw { startPostponedEnterTransition() }

        val binding = FragmentMealAddEditBinding.bind(view)

        binding.apply {
            editTextName.apply {
                setText(viewModel.name)
                requestFocus()
                showSoftKeyboard(this)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.name = text.toString()
                }
            }
            fabSaveMeal.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is AddEditMealViewModel.Event.ShowNameError ->
                            binding.tilMealName.error = getString(it.resId)
                        is AddEditMealViewModel.Event.NavigateBackWithResult -> {
                            binding.editTextName.clearFocus()
                            setFragmentResult(
                                getString(R.string.add_edit_meal_request_key),
                                bundleOf(getString(R.string.result) to it.result)
                            )
                            findNavController().popBackStack()
                        }
                    }
                }
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}