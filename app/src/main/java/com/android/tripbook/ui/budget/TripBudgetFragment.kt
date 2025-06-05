package com.android.tripbook.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tripbook.adapter.BudgetCategoryAdapter
import com.android.tripbook.adapter.ExpenseAdapter
import com.android.tripbook.databinding.FragmentTripBudgetBinding
import com.android.tripbook.viewmodel.BudgetViewModel
import com.android.tripbook.viewmodel.BudgetViewModelFactory
import com.android.tripbook.viewmodel.TripViewModel
import com.android.tripbook.viewmodel.TripViewModelFactory
import com.android.tripbook.ui.budget.AddEditBudgetCategoryDialogFragment
import com.android.tripbook.ui.budget.AddEditExpenseDialogFragment
import java.text.NumberFormat

class TripBudgetFragment : Fragment() {

    private var _binding: FragmentTripBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var tripViewModel: TripViewModel

    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    private val args: TripBudgetFragmentArgs by navArgs()
    private lateinit var currentTripId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTripId = args.tripId

        val budgetViewModelFactory = BudgetViewModelFactory(requireActivity().application, currentTripId)
        budgetViewModel = ViewModelProvider(this, budgetViewModelFactory).get(BudgetViewModel::class.java)

        val tripViewModelFactory = TripViewModelFactory(requireActivity().application)
        tripViewModel = ViewModelProvider(this, tripViewModelFactory).get(TripViewModel::class.java)

        tripViewModel.getTripById(currentTripId).observe(viewLifecycleOwner) { trip ->
            trip?.let {
                binding.textViewTripNameBudget.text = "${it.name} - Budget"
            } ?: run {
                binding.textViewTripNameBudget.text = "Trip Budget"
            }
        }

        val budgetCategoryAdapter = BudgetCategoryAdapter(
            viewLifecycleOwner,
            budgetViewModel,
            onItemClicked = { selectedCategory ->
                val dialog = AddEditBudgetCategoryDialogFragment.newInstance(currentTripId, selectedCategory)
                dialog.show(childFragmentManager, AddEditBudgetCategoryDialogFragment.TAG)
            },
            onExpenseClicked = { selectedExpense ->
                val dialog = AddEditExpenseDialogFragment.newInstance(currentTripId, selectedExpense)
                dialog.show(childFragmentManager, AddEditExpenseDialogFragment.TAG)
            },
            onCategoryDeleteClicked = { categoryToDelete ->
                showDeleteCategoryConfirmationDialog(categoryToDelete)
            },
            onExpenseDeleteClicked = { expenseToDelete ->
                showDeleteExpenseConfirmationDialog(expenseToDelete)
            }
        )
        binding.recyclerViewBudgetCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = budgetCategoryAdapter
        }

        budgetViewModel.budgetCategoriesForTrip.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                budgetCategoryAdapter.submitList(it)
                binding.textViewEmptyCategories.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerViewBudgetCategories.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                updateBudgetSummary()
            }
        }

        budgetViewModel.expensesForTrip.observe(viewLifecycleOwner) { allExpenses ->
            updateBudgetSummary()
        }

        binding.buttonAddBudgetCategory.setOnClickListener {
            val dialog = AddEditBudgetCategoryDialogFragment.newInstance(currentTripId, null)
            dialog.show(childFragmentManager, AddEditBudgetCategoryDialogFragment.TAG)
        }

        binding.buttonAddExpense.setOnClickListener {
            val dialog = AddEditExpenseDialogFragment.newInstance(currentTripId, null)
            dialog.show(childFragmentManager, AddEditExpenseDialogFragment.TAG)
        }
    }

    private fun updateBudgetSummary() {
        val totalPlanned = budgetViewModel.budgetCategoriesForTrip.value?.sumOf { it.plannedAmount } ?: 0.0
        val totalSpent = budgetViewModel.expensesForTrip.value?.sumOf { it.amount } ?: 0.0

        binding.textViewTotalPlanned.text = currencyFormatter.format(totalPlanned)
        binding.textViewTotalSpent.text = currencyFormatter.format(totalSpent)

        val remaining = totalPlanned - totalSpent
        binding.textViewTotalRemaining.text = currencyFormatter.format(remaining)

        if (remaining < 0) {
            binding.textViewTotalRemaining.setTextColor(Color.RED)
            binding.textViewRemainingLabel.text = "Overspent"
        } else {
            binding.textViewTotalRemaining.setTextColor(binding.textViewTotalPlanned.currentTextColor)
            binding.textViewRemainingLabel.text = "Remaining"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

