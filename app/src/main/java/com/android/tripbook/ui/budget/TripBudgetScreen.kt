package com.android.tripbook.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.viewmodel.BudgetViewModel

@Composable
fun BudgetCategoryCard(
    category: BudgetCategory,
    expenses: List<Expense>,
    onAddExpenseClick: () -> Unit,
    onEditCategoryClick: () -> Unit,
    onDeleteCategoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category.name, style = MaterialTheme.typography.titleLarge)
                Row {
                    IconButton(onClick = onEditCategoryClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Category")
                    }
                    IconButton(onClick = onDeleteCategoryClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Category")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val totalSpent = expenses.sumOf { it.amount }
            Text("Planned: ${category.plannedAmount} | Spent: $totalSpent")
            LinearProgressIndicator(
                progress = (totalSpent / category.plannedAmount).toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Expenses:", style = MaterialTheme.typography.titleMedium)
            expenses.forEach { expense ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(expense.description)
                    Text(expense.amount.toString())
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAddExpenseClick, modifier = Modifier.align(Alignment.End)) {
                Text("Add Expense")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBudgetScreen(
    budgetViewModel: BudgetViewModel,
    tripId: String,
    navController: NavController
) {
    val categories by budgetViewModel.budgetCategoriesForTrip.observeAsState(emptyList())
    val expenses by budgetViewModel.expensesForTrip.observeAsState(emptyList())
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedCategoryForExpense by remember { mutableStateOf<BudgetCategory?>(null) }
    var showDeleteCategoryDialog by remember { mutableStateOf<BudgetCategory?>(null) }

    if (showAddCategoryDialog) {
        AddEditBudgetCategoryDialog(
            budgetViewModel = budgetViewModel,
            onDismiss = { showAddCategoryDialog = false }
        )
    }
    
    if (showAddExpenseDialog) {
        AddEditExpenseDialog(
            budgetViewModel = budgetViewModel,
            tripId = tripId,
            onDismiss = { showAddExpenseDialog = false }
        )
    }

    showDeleteCategoryDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = null },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete '${category.name}'? This will also delete all its expenses.") },
            confirmButton = {
                Button(onClick = {
                    budgetViewModel.deleteBudgetCategory(category)
                    showDeleteCategoryDialog = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCategoryDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showAddCategoryDialog = true },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Budget Category")
                }
                FloatingActionButton(onClick = { showAddExpenseDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Expense")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (categories.isEmpty()) {
                item {
                    Text(
                        "No budget categories yet.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(categories) { category ->
                    BudgetCategoryCard(
                        category = category,
                        expenses = expenses.filter { it.categoryId == category.id },
                        onAddExpenseClick = {
                            selectedCategoryForExpense = category
                            showAddExpenseDialog = true
                        },
                        onEditCategoryClick = { /* TODO */ },
                        onDeleteCategoryClick = {
                            budgetViewModel.deleteBudgetCategory(category)
                            showDeleteCategoryDialog = category
                        }
                    )
                }
            }
        }
    }
} 