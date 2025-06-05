package com.android.tripbook.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs // If using Navigation Component and passing tripId as argument
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tripbook.adapter.BudgetCategoryAdapter
import com.android.tripbook.adapter.ExpenseAdapter // If you have a separate general expense list
import com.android.tripbook.databinding.FragmentTripBudgetBinding // ViewBinding
import com.android.tripbook.viewmodel.BudgetViewModel
import com.android.tripbook.viewmodel.BudgetViewModelFactory
import com.android.tripbook.ui.budget.AddEditBudgetCategoryDialogFragment
import com.android.tripbook.ui.budget.AddEditExpenseDialogFragment

class TripBudgetFragment : Fragment() {

    private var _binding: FragmentTripBudgetBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter
    private lateinit var expenseAdapter: ExpenseAdapter // Make this a class-level property if used

    // If using Navigation Component to receive tripId:
    // private val args: TripBudgetFragmentArgs by navArgs() // Assumes you have defined an argument named 'tripId'

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Get tripId ---
        // Placeholder: You need a way to get the current trip's ID.
        // This could come from fragment arguments (Navigation Component), activity intent extras, or a shared ViewModel.
        // For now, let's use a placeholder. Replace this with your actual tripId retrieval.
        val currentTripId = "placeholder_trip_id" // IMPORTANT: Replace this!
        // Example if using NavArgs: val currentTripId = args.tripId


        // --- ViewModel Setup ---
        val viewModelFactory = BudgetViewModelFactory(requireActivity().application, currentTripId)
        budgetViewModel = ViewModelProvider(this, viewModelFactory).get(BudgetViewModel::class.java)

        // --- BudgetCategory RecyclerView Setup ---
        budgetCategoryAdapter = BudgetCategoryAdapter { selectedCategory ->
            val dialog = AddEditBudgetCategoryDialogFragment.newInstance(currentTripId, selectedCategory)
            dialog.show(childFragmentManager, AddEditBudgetCategoryDialogFragment.TAG)
        }
        binding.recyclerViewBudgetCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = budgetCategoryAdapter
        }

        // --- Observe Budget Categories ---
        budgetViewModel.budgetCategoriesForTrip.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                budgetCategoryAdapter.submitList(it)
                // TODO: Update overall budget summary if you have one
            }
        }

        // --- General Expense RecyclerView Setup (IF YOU USE IT) ---
        // If you have a separate RecyclerView in fragment_trip_budget.xml for all expenses:
        // 1. Make sure you have a <androidx.recyclerview.widget.RecyclerView android:id="@+id/recyclerViewExpenses" ... />
        //    in fragment_trip_budget.xml
        // 2. Uncomment and adapt the following:

        /* // UNCOMMENT IF USING A SEPARATE EXPENSE LIST
        expenseAdapter = ExpenseAdapter { selectedExpense ->
            // Handle expense item click -> Show edit dialog
            val dialog = AddEditExpenseDialogFragment.newInstance(currentTripId, selectedExpense)
            dialog.show(childFragmentManager, AddEditExpenseDialogFragment.TAG)
        }
        // Assuming your main layout (fragment_trip_budget.xml) has a second RecyclerView:
        // binding.recyclerViewExpenses.apply { // Make sure this ID exists in your XML
        //     layoutManager = LinearLayoutManager(context)
        //     adapter = expenseAdapter
        // }
        // budgetViewModel.expensesForTrip.observe(viewLifecycleOwner) { expenses ->
        //     expenses?.let {
        //         expenseAdapter.submitList(it)
        //         // TODO: Update overall spending summary
        //     }
        // }
        */

        // --- Button Click Listeners ---
        binding.buttonAddBudgetCategory.setOnClickListener {
            val dialog = AddEditBudgetCategoryDialogFragment.newInstance(currentTripId, null)
            dialog.show(childFragmentManager, AddEditBudgetCategoryDialogFragment.TAG)
        }

        binding.buttonAddExpense.setOnClickListener {
            // Show dialog to add new expense (pass null for expense)
            val dialog = AddEditExpenseDialogFragment.newInstance(currentTripId, null)
            dialog.show(childFragmentManager, AddEditExpenseDialogFragment.TAG)
        }

        // TODO: Populate trip name (e.g., from TripViewModel or passed argument)
        // binding.textViewTripNameBudget.text = "Budget for..."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to prevent memory leaks
    }
}
