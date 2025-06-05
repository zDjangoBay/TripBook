package com.android.tripbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.viewmodel.BudgetViewModel
import java.text.NumberFormat // For formatting currency

class BudgetCategoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val budgetViewModel: BudgetViewModel,
    private val onItemClicked: (BudgetCategory) -> Unit,
    private val onExpenseClicked: (Expense) -> Unit,
    private val onCategoryDeleteClicked: (BudgetCategory) -> Unit,
    private val onExpenseDeleteClicked: (Expense) -> Unit
) : ListAdapter<BudgetCategory, BudgetCategoryAdapter.BudgetCategoryViewHolder>(BudgetCategoryDiffCallback()) {

    // TODO: Add click listeners for item click, edit, delete in later commits

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_category, parent, false)
        return BudgetCategoryViewHolder(
            view, lifecycleOwner, budgetViewModel,
            onItemClicked, onExpenseClicked, onCategoryDeleteClicked, onExpenseDeleteClicked
        )
    }

    override fun onBindViewHolder(holder: BudgetCategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    class BudgetCategoryViewHolder(
        itemView: View,
        private val lifecycleOwner: LifecycleOwner,
        private val budgetViewModel: BudgetViewModel,
        private val onItemClicked: (BudgetCategory) -> Unit,
        private val onExpenseClicked: (Expense) -> Unit,
        private val onCategoryDeleteClicked: (BudgetCategory) -> Unit,
        private val onExpenseDeleteClicked: (Expense) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.textViewCategoryName)
        private val plannedAmountTextView: TextView = itemView.findViewById(R.id.textViewCategoryPlannedAmount)
        private val actualAmountTextView: TextView = itemView.findViewById(R.id.textViewCategoryActualAmount)
        private val deleteCategoryImageView: ImageView = itemView.findViewById(R.id.imageViewDeleteCategory) // Get delete icon
        private val expensesRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerViewCategoryExpenses)
        private val currencyFormatter = NumberFormat.getCurrencyInstance() // Consider locale
        private lateinit var expenseAdapter: ExpenseAdapter // For the nested list

        private var currentCategory: BudgetCategory? = null

        init {
            itemView.setOnClickListener {
                currentCategory?.let { onItemClicked(it) }
            }
            deleteCategoryImageView.setOnClickListener {
                currentCategory?.let { onCategoryDeleteClicked(it) }
            }
        }

        fun bind(budgetCategory: BudgetCategory) {
            currentCategory = budgetCategory
            categoryNameTextView.text = budgetCategory.name
            plannedAmountTextView.text = currencyFormatter.format(budgetCategory.plannedAmount)

            // Setup nested RecyclerView for expenses
            expenseAdapter = ExpenseAdapter(
                onItemClicked = { expense ->
                    onExpenseClicked(expense)
                },
                onDeleteClicked = { expense ->
                    onExpenseDeleteClicked(expense)
                }
            )
            expensesRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = expenseAdapter
                // Optional: Add item decoration if needed for spacing
            }

            // Observe expenses for this specific category
            // and update the expense list and the actual amount spent
            budgetViewModel.getExpensesForTripAndCategory(budgetCategory.tripId, budgetCategory.id)
                .observe(lifecycleOwner) { expenses ->
                    expenses?.let {
                        expenseAdapter.submitList(it)
                        val totalSpent = it.sumOf { expense -> expense.amount }
                        actualAmountTextView.text = currencyFormatter.format(totalSpent)
                    } ?: run {
                        expenseAdapter.submitList(emptyList())
                        actualAmountTextView.text = currencyFormatter.format(0.0)
                    }
                }
        }
    }
}

class BudgetCategoryDiffCallback : DiffUtil.ItemCallback<BudgetCategory>() {
    override fun areItemsTheSame(oldItem: BudgetCategory, newItem: BudgetCategory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BudgetCategory, newItem: BudgetCategory): Boolean {
        return oldItem == newItem
    }
}