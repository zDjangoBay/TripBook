package com.android.tripbook.ui.uis.tripcreation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TravelCompanion
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.StepHeader
import com.android.tripbook.ui.components.TripCreationTextField

@Composable
fun CompanionsStep(
    state: TripCreationState,
    onStateChange: (TripCreationState) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddCompanionDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "Travel Companions",
            subtitle = "Who's joining your adventure? (Optional)",
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
                    .padding(20.dp)
            ) {
                OutlinedButton(
                    onClick = { showAddCompanionDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B73FF)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF6B73FF))
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Travel Companion", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (state.companions.isNotEmpty()) {
                    Text(
                        text = "Travel Companions (${state.companions.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(state.companions) { index, companion ->
                            CompanionCard(
                                companion = companion,
                                onRemove = {
                                    onStateChange(state.copy(
                                        companions = state.companions.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    ))
                                }
                            )
                        }
                    }
                } else {
                    EmptyCompanionState()
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF6B73FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Total Travelers: ${state.companions.size + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (showAddCompanionDialog) {
        AddCompanionDialog(
            onDismiss = { showAddCompanionDialog = false },
            onAddCompanion = { companion ->
                onStateChange(state.copy(companions = state.companions + companion))
                showAddCompanionDialog = false
            }
        )
    }
}

@Composable
private fun EmptyCompanionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No companions added yet",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You can travel solo or add companions to share the adventure",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun CompanionCard(
    companion: TravelCompanion,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF6B73FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(companion.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                companion.email.takeIf { it.isNotEmpty() }?.let {
                    Text(it, fontSize = 14.sp, color = Color.Gray)
                }
                companion.phone.takeIf { it.isNotEmpty() }?.let {
                    Text(it, fontSize = 14.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@Composable
private fun AddCompanionDialog(
    onDismiss: () -> Unit,
    onAddCompanion: (TravelCompanion) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Travel Companion", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TripCreationTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name *",
                    placeholder = "Enter companion's name",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TripCreationTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email (Optional)",
                    placeholder = "Enter email address",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TripCreationTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone (Optional)",
                    placeholder = "Enter phone number"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddCompanion(
                        TravelCompanion(
                            name = name.trim(),
                            email = email.trim(),
                            phone = phone.trim()
                        )
                    )
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B73FF))
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}