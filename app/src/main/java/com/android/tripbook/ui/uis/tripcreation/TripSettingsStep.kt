package com.android.tripbook.ui.uis.tripcreation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.CategoryChip
import com.android.tripbook.ui.components.StepHeader
import com.android.tripbook.ui.components.TripCreationTextField

@Composable
fun TripSettingsStep(
    state: TripCreationState,
    onStateChange: (TripCreationState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "Trip Details",
            subtitle = "Tell us about your trip",
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Trip Name
                TripCreationTextField(
                    value = state.tripName,
                    onValueChange = { onStateChange(state.copy(tripName = it)) },
                    label = "Trip Name *",
                    placeholder = "e.g., Egyptian Pyramids Adventure",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                // Trip Category
                Text(
                    text = "Trip Category",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    items(TripCategory.values()) { category ->
                        CategoryChip(
                            text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                            isSelected = state.category == category,
                            onClick = { onStateChange(state.copy(category = category)) }
                        )
                    }
                }
                
                // Budget
                Text(
                    text = "Estimated Budget (USD)",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = if (state.budget == 0.0) "" else state.budget.toString(),
                    onValueChange = { newValue ->
                        val budget = newValue.toDoubleOrNull() ?: 0.0
                        onStateChange(state.copy(budget = budget))
                    },
                    placeholder = { Text("Enter budget amount") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Budget",
                            tint = Color(0xFF6B73FF)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6B73FF),
                        focusedLabelColor = Color(0xFF6B73FF)
                    )
                )
                
                // Description
                TripCreationTextField(
                    value = state.description,
                    onValueChange = { onStateChange(state.copy(description = it)) },
                    label = "Description (Optional)",
                    placeholder = "Describe your dream trip...",
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                // Trip Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Trip Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        if (state.tripName.isNotEmpty()) {
                            SummaryRow("Name", state.tripName)
                        }
                        
                        SummaryRow("Category", state.category.name.lowercase().replaceFirstChar { it.uppercase() })
                        
                        if (state.budget > 0) {
                            SummaryRow("Budget", "$${state.budget}")
                        }
                        
                        if (state.description.isNotEmpty()) {
                            SummaryRow("Description", state.description.take(50) + if (state.description.length > 50) "..." else "")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Validation Message
                if (state.tripName.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Text(
                            text = "⚠️ Trip name is required to proceed",
                            fontSize = 14.sp,
                            color = Color(0xFFFF8F00),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}
