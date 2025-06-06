package com.android.tripbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.model.Expense
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private val onItemClicked: (Expense) -> Unit,
    private val onDeleteClicked: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view, onItemClicked, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }

    class ExpenseViewHolder(
        itemView: View,
        private val onItemClicked: (Expense) -> Unit,
        private val onDeleteClicked: (Expense) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewExpenseDescription)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewExpenseDate)
        private val amountTextView: TextView = itemView.findViewById(R.id.textViewExpenseAmount)
        private val deleteExpenseImageView: ImageView = itemView.findViewById(R.id.imageViewDeleteExpense)
        private val currencyFormatter = NumberFormat.getCurrencyInstance()
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        private var currentExpense: Expense? = null

        init {
            itemView.setOnClickListener {
                currentExpense?.let { onItemClicked(it) }
            }
            deleteExpenseImageView.setOnClickListener {
                currentExpense?.let { onDeleteClicked(it) }
            }
        }

        fun bind(expense: Expense) {
            currentExpense = expense
            descriptionTextView.text = expense.description
            amountTextView.text = currencyFormatter.format(expense.amount)
            dateTextView.text = dateFormatter.format(Date(expense.date))
        }
    }
}

class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem == newItem
    }
}