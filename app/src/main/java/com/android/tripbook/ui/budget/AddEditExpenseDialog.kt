package com.android.tripbook.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.viewmodel.BudgetViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
    budgetViewModel: BudgetViewModel,
    tripId: String,
    existingExpense: Expense? = null,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf(existingExpense?.description ?: "") }
    var amount by remember { mutableStateOf(existingExpense?.amount?.toString() ?: "") }
    var dateString by remember { mutableStateOf(existingExpense?.date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it)) } ?: "") }
    val categories: List<BudgetCategory> by budgetViewModel.budgetCategoriesForTrip.observeAsState(emptyList())
    var selectedCategory: BudgetCategory? by remember { mutableStateOf(categories.find { it.id == existingExpense?.categoryId } ?: categories.firstOrNull()) }

    var descriptionError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (existingExpense == null) "Add Expense" else "Edit Expense",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    isError = descriptionError != null,
                    supportingText = { descriptionError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    supportingText = { amountError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                     isError = dateError != null,
                    supportingText = { dateError?.let { Text(it) } }
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        descriptionError = if (description.isBlank()) "Description cannot be empty" else null
                        val amountValue = amount.toDoubleOrNull()
                        amountError = if (amountValue == null || amountValue <= 0) "Invalid amount" else null
                        
                        val dateLong = try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdf.parse(dateString)?.time
                        } catch (e: Exception) {
                            null
                        }
                        dateError = if (dateLong == null) "Invalid date format" else null
                        
                        if (descriptionError == null && amountError == null && dateError == null && selectedCategory != null) {
                            if (existingExpense == null) {
                                val newExpense = Expense(
                                    tripId = tripId,
                                    categoryId = selectedCategory!!.id,
                                    description = description,
                                    amount = amountValue!!,
                                    date = dateLong!!
                                )
                                budgetViewModel.insertExpense(newExpense)
                            } else {
                                val updatedExpense = existingExpense.copy(
                                    categoryId = selectedCategory!!.id,
                                    description = description,
                                    amount = amountValue!!,
                                    date = dateLong!!
                                )
                                budgetViewModel.updateExpense(updatedExpense)
                            }
                            onDismiss()
                        }
                    }) {
                        Text(if (existingExpense == null) "Add" else "Save")
                    }
                }
            }
        }
    }
} 