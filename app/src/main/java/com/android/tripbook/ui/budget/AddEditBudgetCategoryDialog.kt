package com.android.tripbook.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.utils.CurrencyUtils
import com.android.tripbook.viewmodel.BudgetViewModel

@Composable
fun AddEditBudgetCategoryDialog(
    budgetViewModel: BudgetViewModel,
    tripId: String,
    existingCategory: BudgetCategory? = null,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(existingCategory?.name ?: "") }
    var plannedAmount by remember { mutableStateOf(existingCategory?.plannedAmount?.toString() ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

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
                    text = if (existingCategory == null) "Ajouter Catégorie Budget" else "Modifier Catégorie Budget",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la catégorie (ex: Transport, Hébergement)") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = plannedAmount,
                    onValueChange = { plannedAmount = it },
                    label = { Text("Budget prévu (CFA)") },
                    placeholder = { Text("ex: 50000") },
                    isError = amountError != null,
                    supportingText = { amountError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        nameError = if (name.isBlank()) "Le nom ne peut pas être vide" else null
                        val amount = plannedAmount.toDoubleOrNull()
                        amountError = if (amount == null || amount < 0) "Montant invalide" else null

                        if (nameError == null && amountError == null) {
                            if (existingCategory == null) {
                                val newCategory = BudgetCategory(tripId = tripId, name = name, plannedAmount = amount!!)
                                budgetViewModel.insertBudgetCategory(newCategory)
                            } else {
                                val updatedCategory = existingCategory.copy(name = name, plannedAmount = amount!!)
                                budgetViewModel.updateBudgetCategory(updatedCategory)
                            }
                            onDismiss()
                        }
                    }) {
                        Text(if (existingCategory == null) "Ajouter" else "Sauvegarder")
                    }
                }
            }
        }
    }
} 