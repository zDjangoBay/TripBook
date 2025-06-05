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
import android.widget.Toast // For feedback


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
            .setTitle(dialogTitle)
            .setPositiveButton(if (existingCategory == null) "Add" else "Save", null) // Set null listener initially
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                // Override positive button click to control dialog dismissal based on validation
                setOnShowListener { dialogInterface ->
                    val positiveButton = (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setOnClickListener {
                        validateAndSaveCategory()
                    }
                }
            }
    }

    private fun validateAndSaveCategory() {
        val name = binding.editTextCategoryName.text.toString().trim()
        val plannedAmountStr = binding.editTextPlannedAmount.text.toString().trim()

        if (name.isEmpty()) {
            binding.textFieldCategoryNameLayout.error = "Category name cannot be empty"
            return
        } else {
            binding.textFieldCategoryNameLayout.error = null
        }

        // Check for duplicate category name (only if adding new or name changed)
        val isNewCategory = existingCategory == null
        val nameChanged = existingCategory != null && existingCategory!!.name != name

        if (isNewCategory || nameChanged) {
            val categories = budgetViewModel.budgetCategoriesForTrip.value ?: emptyList()
            if (categories.any { it.name.equals(name, ignoreCase = true) && it.id != existingCategory?.id }) {
                binding.textFieldCategoryNameLayout.error = "This category name already exists for this trip."
                return
            } else {
                binding.textFieldCategoryNameLayout.error = null
            }
        }

        val plannedAmount = plannedAmountStr.toDoubleOrNull()
        if (plannedAmount == null || plannedAmount < 0) {
            binding.textFieldPlannedAmountLayout.error = "Please enter a valid planned amount"
            return
        } else {
            binding.textFieldPlannedAmountLayout.error = null
        }

        // If all validations pass:
        if (existingCategory == null) {
            val newCategory = BudgetCategory(tripId = currentTripId, name = name, plannedAmount = plannedAmount)
            budgetViewModel.insertBudgetCategory(newCategory)
            Toast.makeText(context, "Category '$name' added", Toast.LENGTH_SHORT).show()
        } else {
            val updatedCategory = existingCategory!!.copy(name = name, plannedAmount = plannedAmount)
            budgetViewModel.updateBudgetCategory(updatedCategory)
            Toast.makeText(context, "Category '$name' updated", Toast.LENGTH_SHORT).show()
        }
        dismiss() // Dismiss dialog only if save is successful
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to prevent memory leaks from view binding in DialogFragments
    }
}
