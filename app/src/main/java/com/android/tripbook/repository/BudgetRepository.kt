package com.android.tripbook.repository

import androidx.lifecycle.LiveData
import com.android.tripbook.dao.BudgetCategoryDao
import com.android.tripbook.dao.ExpenseDao
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BudgetRepository(
    private val budgetCategoryDao: BudgetCategoryDao,
    private val expenseDao: ExpenseDao
) {

    // --- BudgetCategory Operations ---

    fun getBudgetCategoriesForTrip(tripId: String): LiveData<List<BudgetCategory>> {
        return budgetCategoryDao.getCategoriesForTrip(tripId)
    }

    suspend fun insertBudgetCategory(budgetCategory: BudgetCategory) {
        withContext(Dispatchers.IO) {
            budgetCategoryDao.insert(budgetCategory)
        }
    }

    suspend fun updateBudgetCategory(budgetCategory: BudgetCategory) {
        withContext(Dispatchers.IO) {
            budgetCategoryDao.update(budgetCategory)
        }
    }

    suspend fun deleteBudgetCategory(budgetCategory: BudgetCategory) {
        withContext(Dispatchers.IO) {
            budgetCategoryDao.delete(budgetCategory)
        }
    }

    fun getBudgetCategoryById(id: Long): LiveData<BudgetCategory?> {
        return budgetCategoryDao.getCategoryById(id)
    }

    // --- Expense Operations ---

    fun getExpensesForTrip(tripId: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesForTrip(tripId)
    }

    fun getExpensesForCategory(categoryId: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesForCategory(categoryId)
    }
    
    fun getExpensesForTripAndCategory(tripId: String, categoryId: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesByTripAndCategory(tripId, categoryId)
    }

    suspend fun insertExpense(expense: Expense) {
        withContext(Dispatchers.IO) {
            expenseDao.insert(expense)
        }
    }

    suspend fun updateExpense(expense: Expense) {
        withContext(Dispatchers.IO) {
            expenseDao.update(expense)
        }
    }

    suspend fun deleteExpense(expense: Expense) {
        withContext(Dispatchers.IO) {
            expenseDao.delete(expense)
        }
    }
    
    fun getExpenseById(id: Long): LiveData<Expense?> {
        return expenseDao.getExpenseById(id)
    }
}
