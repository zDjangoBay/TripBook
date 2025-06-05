package com.android.tripbook.ui.budget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.databinding.DialogAddEditBudgetCategoryBinding
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.viewmodel.BudgetViewModel
import com.android.tripbook.viewmodel.BudgetViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AddEditBudgetCategoryDialogFragment : DialogFragment() {

    private var _binding: DialogAddEditBudgetCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetViewModel: BudgetViewModel
    private var existingCategory: BudgetCategory? = null
    private lateinit var currentTripId: String // Needs to be passed or retrieved

    companion object {
        const val TAG = "AddEditBudgetCategoryDialog"
        private const val ARG_TRIP_ID = "trip_id"
        private const val ARG_CATEGORY = "category_to_edit"

        fun newInstance(tripId: String, category: BudgetCategory? = null): AddEditBudgetCategoryDialogFragment {
            val args = Bundle().apply {
                putString(ARG_TRIP_ID, tripId)
                putParcelable(ARG_CATEGORY, category) // BudgetCategory needs to be Parcelable
            }
            val fragment = AddEditBudgetCategoryDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditBudgetCategoryBinding.inflate(LayoutInflater.from(context))

        currentTripId = requireArguments().getString(ARG_TRIP_ID)!!
        existingCategory = requireArguments().getParcelable(ARG_CATEGORY) // BudgetCategory needs to be Parcelable for this

        val viewModelFactory = BudgetViewModelFactory(requireActivity().application, currentTripId)
        budgetViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(BudgetViewModel::class.java)

        val dialogTitle = if (existingCategory == null) "Add Budget Category" else "Edit Budget Category"
        binding.textViewAddEditCategoryTitle.text = dialogTitle
        existingCategory?.let {
            binding.editTextCategoryName.setText(it.name)
            binding.editTextPlannedAmount.setText(it.plannedAmount.toString())
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(dialogTitle) // Can also set title on the TextView directly as done above
            .setPositiveButton(if (existingCategory == null) "Add" else "Save") { _, _ ->
                saveCategory()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun saveCategory() {
        val name = binding.editTextCategoryName.text.toString().trim()
        val plannedAmountStr = binding.editTextPlannedAmount.text.toString().trim()

        if (name.isEmpty()) {
            binding.textFieldCategoryNameLayout.error = "Category name cannot be empty"
            return
        } else {
            binding.textFieldCategoryNameLayout.error = null
        }

        val plannedAmount = plannedAmountStr.toDoubleOrNull()
        if (plannedAmount == null || plannedAmount < 0) {
            binding.textFieldPlannedAmountLayout.error = "Please enter a valid planned amount"
            return
        } else {
            binding.textFieldPlannedAmountLayout.error = null
        }

        if (existingCategory == null) {
            // Add new category
            val newCategory = BudgetCategory(
                tripId = currentTripId, // Ensure tripId is associated
                name = name,
                plannedAmount = plannedAmount
            )
            budgetViewModel.insertBudgetCategory(newCategory)
        } else {
            // Update existing category
            val updatedCategory = existingCategory!!.copy(
                name = name,
                plannedAmount = plannedAmount
                // tripId remains the same
            )
            budgetViewModel.updateBudgetCategory(updatedCategory)
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to prevent memory leaks from view binding in DialogFragments
    }
}
