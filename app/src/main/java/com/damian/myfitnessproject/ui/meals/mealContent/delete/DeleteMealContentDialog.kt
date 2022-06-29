package com.damian.myfitnessproject.ui.meals.mealContent.delete

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteMealContentDialog : DialogFragment() {

    private val viewModel: DeleteMealContentViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage(viewModel.message)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onPositiveButtonClick()
            }
        return builder.show()
    }
}