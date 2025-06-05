package com.android.tripbook.ui.budget

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
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
        existingExpense = requireArguments().getParcelable(ARG_EXPENSE) // Expense needs to be Parcelable

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
            // Category will be set by observing LiveData for spinner
        } ?: run {
            // For new expense, set default date to today
             binding.editTextExpenseDate.setText(dateFormatter.format(Date()))
        }


        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(dialogTitle)
            .setPositiveButton(if (existingExpense == null) "Add" else "Save") { _, _ ->
                saveExpense()
            }
            .setNegativeButton("Cancel", null)
            .create()
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


    private fun saveExpense() {
        val description = binding.editTextExpenseDescription.text.toString().trim()
        val amountStr = binding.editTextExpenseAmount.text.toString().trim()
        val dateStr = binding.editTextExpenseDate.text.toString().trim()

        if (description.isEmpty()) {
            binding.textFieldExpenseDescriptionLayout.error = "Description cannot be empty"
            return
        } else {
            binding.textFieldExpenseDescriptionLayout.error = null
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) { // Expenses should be positive
            binding.textFieldExpenseAmountLayout.error = "Please enter a valid positive amount"
            return
        } else {
            binding.textFieldExpenseAmountLayout.error = null
        }

        val selectedDateInMillis = try {
            dateFormatter.parse(dateStr)?.time ?: throw NullPointerException()
        } catch (e: Exception) {
            binding.textFieldExpenseDateLayout.error = "Please select a valid date"
            return
        }
        binding.textFieldExpenseDateLayout.error = null


        val selectedCategoryPosition = binding.spinnerExpenseCategory.selectedItemPosition
        if (allCategoriesForTrip.isEmpty() || selectedCategoryPosition == Spinner.INVALID_POSITION || selectedCategoryPosition >= allCategoriesForTrip.size) {
            Toast.makeText(context, "Please select a valid category or add one first.", Toast.LENGTH_LONG).show()
            // Potentially highlight spinner or show error on its TextInputLayout if you wrap it
            return
        }
        val selectedCategory = allCategoriesForTrip[selectedCategoryPosition]


        if (existingExpense == null) {
            val newExpense = Expense(
                tripId = currentTripId,
                categoryId = selectedCategory.id,
                description = description,
                amount = amount,
                date = selectedDateInMillis
            )
            budgetViewModel.insertExpense(newExpense)
        } else {
            val updatedExpense = existingExpense!!.copy(
                // tripId should not change for an existing expense
                categoryId = selectedCategory.id,
                description = description,
                amount = amount,
                date = selectedDateInMillis
            )
            budgetViewModel.updateExpense(updatedExpense)
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
