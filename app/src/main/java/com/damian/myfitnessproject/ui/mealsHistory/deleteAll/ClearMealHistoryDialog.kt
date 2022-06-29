package com.damian.myfitnessproject.ui.mealsHistory.deleteAll

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClearMealHistoryDialog : DialogFragment() {

    private val viewModel: ClearMealHistoryViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you rely wont to clear meals history?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onPositiveButtonClick()
            }
        return builder.show()
    }
}