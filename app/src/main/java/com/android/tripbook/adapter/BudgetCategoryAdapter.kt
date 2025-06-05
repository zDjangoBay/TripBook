package com.android.tripbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.model.BudgetCategory
import java.text.NumberFormat // For formatting currency

class BudgetCategoryAdapter : ListAdapter<BudgetCategory, BudgetCategoryAdapter.BudgetCategoryViewHolder>(BudgetCategoryDiffCallback()) {

    // TODO: Add click listeners for item click, edit, delete in later commits

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_category, parent, false)
        return BudgetCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetCategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    class BudgetCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.textViewCategoryName)
        private val plannedAmountTextView: TextView = itemView.findViewById(R.id.textViewCategoryPlannedAmount)
        private val actualAmountTextView: TextView = itemView.findViewById(R.id.textViewCategoryActualAmount)
        private val currencyFormatter = NumberFormat.getCurrencyInstance() // Consider locale

        fun bind(budgetCategory: BudgetCategory) {
            categoryNameTextView.text = budgetCategory.name
            plannedAmountTextView.text = currencyFormatter.format(budgetCategory.plannedAmount)

            // TODO: Calculate and display actual spent amount for this category in a later commit.
            // For now, let's show 0 or a placeholder.
            actualAmountTextView.text = currencyFormatter.format(0.0) // Placeholder
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