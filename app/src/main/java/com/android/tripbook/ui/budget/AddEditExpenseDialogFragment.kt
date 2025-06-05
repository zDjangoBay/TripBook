package com.android.tripbook.ui.budget

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.R
import com.android.tripbook.databinding.DialogAddEditExpenseBinding
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.viewmodel.BudgetViewModel
import com.android.tripbook.viewmodel.BudgetViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditExpenseDialogFragment : DialogFragment() {

    private var _binding: DialogAddEditExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetViewModel: BudgetViewModel
    private var existingExpense: Expense? = null
    private lateinit var currentTripId: String

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var allCategoriesForTrip: List<BudgetCategory> = emptyList()

    companion object {
        const val TAG = "AddEditExpenseDialog"
        private const val ARG_TRIP_ID = "trip_id"
        private const val ARG_EXPENSE = "expense_to_edit"

        fun newInstance(tripId: String, expense: Expense? = null): AddEditExpenseDialogFragment {
            val args = Bundle().apply {
                putString(ARG_TRIP_ID, tripId)
                putParcelable(ARG_EXPENSE, expense) // Expense needs to be Parcelable
            }
            val fragment = AddEditExpenseDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditExpenseBinding.inflate(LayoutInflater.from(context))

        currentTripId = requireArguments().getString(ARG_TRIP_ID)!!
        existingExpense = requireArguments().getParcelable(ARG_EXPENSE)

        val viewModelFactory = BudgetViewModelFactory(requireActivity().application, currentTripId)
        budgetViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(BudgetViewModel::class.java)

        setupDatePicker()
        setupCategorySpinner()

        val dialogTitle = if (existingExpense == null) "Add Expense" else "Edit Expense"
        binding.textViewAddEditExpenseTitle.text = dialogTitle
        existingExpense?.let {
            binding.editTextExpenseDescription.setText(it.description)
            binding.editTextExpenseAmount.setText(it.amount.toString())
            calendar.timeInMillis = it.date
            binding.editTextExpenseDate.setText(dateFormatter.format(calendar.time))
            // Category selection will be handled by setupCategorySpinner observing LiveData
        } ?: run {
            binding.editTextExpenseDate.setText(dateFormatter.format(Date())) // Default to today for new expense
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(dialogTitle)
            .setPositiveButton(if (existingExpense == null) "Add" else "Save", null) // Null listener initially
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener { dialogInterface ->
                    val positiveButton = (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setOnClickListener {
                        validateAndSaveExpense()
                    }
                }
            }
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.editTextExpenseDate.setText(dateFormatter.format(calendar.time))
        }

        binding.editTextExpenseDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
         binding.textFieldExpenseDateLayout.setEndIconOnClickListener { // if you have an icon
            binding.editTextExpenseDate.performClick()
        }
    }

    private fun setupCategorySpinner() {
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerExpenseCategory.adapter = spinnerAdapter

        budgetViewModel.budgetCategoriesForTrip.observe(this) { categories ->
            allCategoriesForTrip = categories ?: emptyList()
            val categoryNames = categories?.map { it.name } ?: listOf("No categories available")
            spinnerAdapter.clear()
            spinnerAdapter.addAll(categoryNames)
            spinnerAdapter.notifyDataSetChanged()

            // If editing an existing expense, try to select its category
            existingExpense?.let { expense ->
                val categoryIndex = allCategoriesForTrip.indexOfFirst { it.id == expense.categoryId }
                if (categoryIndex != -1) {
                    binding.spinnerExpenseCategory.setSelection(categoryIndex)
                }
            }
        }
    }

    private fun validateAndSaveExpense() {
        val description = binding.editTextExpenseDescription.text.toString().trim()
        val amountStr = binding.editTextExpenseAmount.text.toString().trim()
        val dateStr = binding.editTextExpenseDate.text.toString().trim()

        // Validate Description
        if (description.isEmpty()) {
            binding.textFieldExpenseDescriptionLayout.error = "Description cannot be empty"
            return
        } else {
            binding.textFieldExpenseDescriptionLayout.error = null
        }

        // Validate Amount
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) { // Expenses should typically be positive
            binding.textFieldExpenseAmountLayout.error = "Please enter a valid positive amount"
            return
        } else {
            binding.textFieldExpenseAmountLayout.error = null
        }

        // Validate Date
        val selectedDateInMillis: Long
        try {
            val parsedDate = dateFormatter.parse(dateStr)
            if (parsedDate == null) {
                binding.textFieldExpenseDateLayout.error = "Please select a valid date"
                return
            }
            selectedDateInMillis = parsedDate.time
            binding.textFieldExpenseDateLayout.error = null
        } catch (e: Exception) {
            binding.textFieldExpenseDateLayout.error = "Invalid date format"
            return
        }

        // Validate Category Spinner
        val selectedCategoryPosition = binding.spinnerExpenseCategory.selectedItemPosition
        if (allCategoriesForTrip.isEmpty() || selectedCategoryPosition == Spinner.INVALID_POSITION || selectedCategoryPosition >= allCategoriesForTrip.size) {
            Toast.makeText(context, "Please select a valid category. Add one if none exist.", Toast.LENGTH_LONG).show()
            // Optionally, you could try to set an error on the Spinner's TextInputLayout if you wrap it,
            // or just rely on the Toast for now.
            return
        }
        val selectedCategory = allCategoriesForTrip[selectedCategoryPosition]


        // All validations passed, proceed to save
        if (existingExpense == null) {
            val newExpense = Expense(
                tripId = currentTripId,
                categoryId = selectedCategory.id,
                description = description,
                amount = amount,
                date = selectedDateInMillis
            )
            budgetViewModel.insertExpense(newExpense)
            Toast.makeText(context, "Expense '$description' added", Toast.LENGTH_SHORT).show()
        } else {
            val updatedExpense = existingExpense!!.copy(
                categoryId = selectedCategory.id,
                description = description,
                amount = amount,
                date = selectedDateInMillis
            )
            budgetViewModel.updateExpense(updatedExpense)
            Toast.makeText(context, "Expense '$description' updated", Toast.LENGTH_SHORT).show()
        }
        dismiss() // Dismiss dialog only if save is successful
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
