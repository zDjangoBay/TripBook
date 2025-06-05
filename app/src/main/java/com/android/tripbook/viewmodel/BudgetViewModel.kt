package com.android.tripbook.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.android.tripbook.database.AppDatabase
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application, private val tripId: String) : AndroidViewModel(application) {

    private val repository: BudgetRepository

    val budgetCategoriesForTrip: LiveData<List<BudgetCategory>>
    val expensesForTrip: LiveData<List<Expense>>

    init {
        val budgetCategoryDao = AppDatabase.getDatabase(application).budgetCategoryDao()
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = BudgetRepository(budgetCategoryDao, expenseDao)

        budgetCategoriesForTrip = repository.getBudgetCategoriesForTrip(tripId)
        expensesForTrip = repository.getExpensesForTrip(tripId)
    }

    // --- BudgetCategory Operations ---

    fun insertBudgetCategory(budgetCategory: BudgetCategory) = viewModelScope.launch {
        repository.insertBudgetCategory(budgetCategory.copy(tripId = this@BudgetViewModel.tripId)) // Ensure tripId is set
    }

    fun updateBudgetCategory(budgetCategory: BudgetCategory) = viewModelScope.launch {
        repository.updateBudgetCategory(budgetCategory)
    }

    fun deleteBudgetCategory(budgetCategory: BudgetCategory) = viewModelScope.launch {
        repository.deleteBudgetCategory(budgetCategory)
    }

    fun getBudgetCategoryById(id: Long): LiveData<BudgetCategory> {
        return repository.getBudgetCategoryById(id)
    }

    // --- Expense Operations ---

    fun getExpensesForCategory(categoryId: Long): LiveData<List<Expense>> {
        return repository.getExpensesForCategory(categoryId)
    }

    fun getExpensesForTripAndCategory(categoryId: Long): LiveData<List<Expense>> {
        return repository.getExpensesForTripAndCategory(tripId, categoryId)
    }

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        // Ensure tripId is set on the expense, it might already be there if created carefully,
        // but this is a good place to enforce it if it's linked to this ViewModel's tripId.
        repository.insertExpense(expense.copy(tripId = this@BudgetViewModel.tripId))
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    fun getExpenseById(id: Long): LiveData<Expense> {
        return repository.getExpenseById(id)
    }
}

@Suppress("UNCHECKED_CAST")
class BudgetViewModelFactory(
    private val application: Application,
    private val tripId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            return BudgetViewModel(application, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}