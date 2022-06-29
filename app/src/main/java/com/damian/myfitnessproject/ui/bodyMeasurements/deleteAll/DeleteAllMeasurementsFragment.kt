package com.damian.myfitnessproject.ui.bodyMeasurements.deleteAll

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllMeasurementsFragment : DialogFragment() {

    private val viewModel: DeleteAllMeasurementsViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you rely wont to clear measurements history?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onPositiveButtonClick()
            }
        return builder.show()
    }
}