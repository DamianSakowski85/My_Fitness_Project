package com.damian.myfitnessproject.ui.foods.addEdit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentFoodAddEditBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditFoodFragment : Fragment(R.layout.fragment_food_add_edit) {

    private val viewModel: AddEditFoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ true
        )
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
            /* forward= */ false
        )

        enterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
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
        val binding = FragmentFoodAddEditBinding.bind(view)

        binding.apply {

            editTextName.apply {
                setText(viewModel.name)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.name = text.toString()
                }
            }
            editTextDesc.apply {
                setText(viewModel.description)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.description = text.toString()
                }
            }
            editTextCalories.apply {
                setText(viewModel.cal)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.cal = text.toString()
                }
            }
            editTextFats.apply {
                setText(viewModel.fats)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.fats = text.toString()
                }
            }
            editTextCarbs.apply {
                setText(viewModel.carbs)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.carbs = text.toString()
                }
            }
            editTextProteins.apply {
                setText(viewModel.proteins)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.proteins = text.toString()
                }
            }
            btSave.setOnClickListener {
                viewModel.onSaveClick()
            }
            btAddImage.setOnClickListener {
                viewModel.onTakeImageClick()
            }
        }

        viewModel.image.observe(viewLifecycleOwner) {
            binding.img.setImageBitmap(it)
        }

        setFragmentResultListener(TakePhotoFragment.REQUEST_KEY) { _, bundle ->
            val byteArray = bundle.getByteArray(TakePhotoFragment.KEY)
            viewModel.onByteArrayResult(byteArray)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is AddEditFoodViewModel.Event.ShowNameError ->
                            binding.layoutName.error = resources.getString(it.resId)

                        is AddEditFoodViewModel.Event.ShowCalError ->
                            binding.layoutCal.error = resources.getString(it.resId)

                        is AddEditFoodViewModel.Event.ShowFatsError ->
                            binding.layoutFats.error = resources.getString(it.resId)

                        is AddEditFoodViewModel.Event.ShowCarbsError ->
                            binding.layoutCarbs.error = resources.getString(it.resId)

                        is AddEditFoodViewModel.Event.ShowProteinsError ->
                            binding.layoutProteins.error = resources.getString(it.resId)

                        is AddEditFoodViewModel.Event.ShowMacroError ->
                            binding.tvMacroError.text = resources.getString(it.resId)

                        is AddEditFoodViewModel.Event.OpenPhotoDialog ->
                            findNavController().navigate(
                                AddEditFoodFragmentDirections
                                    .actionAddEditFoodFragmentToTakePhotoFragment()
                            )

                        is AddEditFoodViewModel.Event.NavBackWithResult -> {
                            binding.editTextName.clearFocus()
                            setFragmentResult(
                                getString(R.string.add_edit_food_request_key),
                                bundleOf(getString(R.string.result) to it.result)
                            )
                            findNavController().popBackStack()
                        }

                    }
                }
        }

        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}