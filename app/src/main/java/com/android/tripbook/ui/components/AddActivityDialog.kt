package com.android.tripbook.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.ui.theme.TripBookColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun AddActivityDialog(
    tripId: String,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onActivityAdded: (ItineraryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    var duration by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ItineraryType.ACTIVITY) }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Activity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TripBookColors.TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TripBookColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date display
                Text(
                    text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))}",
                    fontSize = 14.sp,
                    color = TripBookColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Activity Type Selection
                Text(
                    text = "Activity Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TripBookColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActivityTypeChip(
                        type = ItineraryType.ACTIVITY,
                        icon = Icons.Default.Place,
                        label = "Activity",
                        isSelected = selectedType == ItineraryType.ACTIVITY,
                        onClick = { selectedType = ItineraryType.ACTIVITY },
                        modifier = Modifier.weight(1f)
                    )
                    ActivityTypeChip(
                        type = ItineraryType.ACCOMMODATION,
                        icon = Icons.Default.Hotel,
                        label = "Hotel",
                        isSelected = selectedType == ItineraryType.ACCOMMODATION,
                        onClick = { selectedType = ItineraryType.ACCOMMODATION },
                        modifier = Modifier.weight(1f)
                    )
                    ActivityTypeChip(
                        type = ItineraryType.TRANSPORTATION,
                        icon = Icons.Default.DirectionsCar,
                        label = "Transport",
                        isSelected = selectedType == ItineraryType.TRANSPORTATION,
                        onClick = { selectedType = ItineraryType.TRANSPORTATION },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                TripBookTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title",
                    placeholder = "Enter activity title"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location
                TripBookTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "Location",
                    placeholder = "Enter location"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Time and Duration Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TripBookTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = "Time",
                        placeholder = "09:00",
                        modifier = Modifier.weight(1f)
                    )
                    TripBookTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = "Duration",
                        placeholder = "2 hours",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cost
                TripBookTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = "Cost (FCFA)",
                    placeholder = "0"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                TripBookTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    placeholder = "Enter activity description",
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notes
                TripBookTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    placeholder = "Additional notes (optional)",
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TripBookColors.TextSecondary
                        )
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val newActivity = ItineraryItem(
                                    id = UUID.randomUUID().toString(),
                                    tripId = tripId,
                                    date = selectedDate,
                                    time = time,
                                    title = title,
                                    location = location,
                                    type = selectedType,
                                    description = description,
                                    duration = duration,
                                    cost = cost.toDoubleOrNull() ?: 0.0,
                                    notes = notes
                                )
                                onActivityAdded(newActivity)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TripBookColors.Primary
                        ),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Add Activity", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityTypeChip(
    type: ItineraryType,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        when (type) {
            ItineraryType.ACTIVITY -> TripBookColors.Primary
            ItineraryType.ACCOMMODATION -> Color(0xFF00CC66)
            ItineraryType.TRANSPORTATION -> Color(0xFFFF9500)
        }
    } else {
        Color.White
    }
    
    val contentColor = if (isSelected) Color.White else TripBookColors.TextSecondary

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (!isSelected) BorderStroke(1.dp, TripBookColors.Border) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
