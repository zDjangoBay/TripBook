package com.android.tripbook.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.ui.theme.TripBookColors
import com.android.tripbook.viewmodel.TripStatistics
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun TripStatisticsCard(
    statistics: TripStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Trip Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TripBookColors.TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.DateRange,
                    value = "${statistics.totalDays}",
                    label = "Days",
                    color = TripBookColors.Primary,
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    icon = Icons.Default.Place,
                    value = "${statistics.activitiesCount}",
                    label = "Activities",
                    color = Color(0xFF00CC66),
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    value = "${statistics.completedActivities}",
                    label = "Completed",
                    color = Color(0xFFE91E63),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TripBookColors.TextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TripBookColors.TextSecondary
        )
    }
}

@Composable
fun DateSelector(
    dates: List<LocalDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(dates) { date ->
            DateChip(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) TripBookColors.Primary else Color.White
    val textColor = if (isSelected) Color.White else TripBookColors.TextPrimary

    Card(
        modifier = Modifier
            .clickable { onClick() }
            .width(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMM")),
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.8f)
            )
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEE")),
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun DayTimelineView(
    date: LocalDate,
    activities: List<ItineraryItem>,
    onActivityClick: (ItineraryItem) -> Unit,
    onAddActivityClick: () -> Unit,
    onUpdateActivityClick: (ItineraryItem) -> Unit, // New callback for update
    onDeleteActivityClick: (String) -> Unit,       // New callback for delete
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Date header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TripBookColors.TextPrimary
            )

            IconButton(
                onClick = onAddActivityClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(TripBookColors.Primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Activity",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (activities.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = "No activities",
                        tint = TripBookColors.TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No activities planned for this day",
                        fontSize = 16.sp,
                        color = TripBookColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to add your first activity",
                        fontSize = 14.sp,
                        color = TripBookColors.TextSecondary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Activities timeline
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityTimelineCard(
                        activity = activity,
                        onClick = { onActivityClick(activity) },
                        onUpdateClick = onUpdateActivityClick, // Pass callback
                        onDeleteClick = onDeleteActivityClick  // Pass callback
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityTimelineCard(
    activity: ItineraryItem,
    onClick: () -> Unit,
    onUpdateClick: (ItineraryItem) -> Unit, // New parameter
    onDeleteClick: (String) -> Unit         // New parameter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Time and type indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = activity.time,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            when (activity.type) {
                                ItineraryType.ACTIVITY -> TripBookColors.Primary
                                ItineraryType.ACCOMMODATION -> Color(0xFF00CC66)
                                ItineraryType.TRANSPORTATION -> Color(0xFFFF9500)
                            },
                            CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.TextPrimary
                )
                if (activity.location.isNotEmpty()) {
                    Text(
                        text = activity.location,
                        fontSize = 14.sp,
                        color = TripBookColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (activity.description.isNotEmpty()) {
                    Text(
                        text = activity.description,
                        fontSize = 14.sp,
                        color = TripBookColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (activity.duration.isNotEmpty()) {
                    Text(
                        text = "Duration: ${activity.duration}",
                        fontSize = 12.sp,
                        color = TripBookColors.TextSecondary.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Completion status and action buttons
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (activity.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF00CC66),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Space between status and buttons
                Row {
                    IconButton(onClick = { onUpdateClick(activity) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Update",
                            tint = TripBookColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { onDeleteClick(activity.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityDialog(
    item: ItineraryItem,
    onDismiss: () -> Unit,
    onSave: (ItineraryItem) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var time by remember { mutableStateOf(item.time) }
    var location by remember { mutableStateOf(item.location) }
    var description by remember { mutableStateOf(item.description) }
    var duration by remember { mutableStateOf(item.duration) }
    var selectedType by remember { mutableStateOf(item.type) }
    var isCompleted by remember { mutableStateOf(item.isCompleted) }

    // State for validation errors
    var titleError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }

    // State to control the visibility of the TimePicker dialog
    var showTimePicker by remember { mutableStateOf(false) }

    // Initialize TimePickerState from the item's time
    val initialTime = try {
        LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: DateTimeParseException) {
        LocalTime.now() // Default to current time if parsing fails
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Activity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TripBookColors.TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = ""
                    },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError.isNotEmpty(),
                    supportingText = { if (titleError.isNotEmpty()) Text(titleError, color = MaterialTheme.colorScheme.error) },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Time - now with TimePicker integration
                OutlinedTextField(
                    value = time,
                    onValueChange = { /* Read-only, value set by TimePicker */ },
                    label = { Text("Time") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }, // Open time picker on click
                    readOnly = true, // Make it read-only
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Select Time",
                            modifier = Modifier.clickable { showTimePicker = true }
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        locationError = ""
                    },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = locationError.isNotEmpty(),
                    supportingText = { if (locationError.isNotEmpty()) Text(locationError, color = MaterialTheme.colorScheme.error) },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Duration
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Type selection
                Text(
                    text = "Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ItineraryType.values().forEach { type ->
                        TripBookFilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = type.name
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Is Completed Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = TripBookColors.Primary
                        )
                    )
                    Text(
                        text = "Mark as Completed",
                        fontSize = 16.sp,
                        color = TripBookColors.TextPrimary
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete Button
                TextButton(onClick = { onDelete(item.id) }) {
                    Text("Delete", color = Color.Red)
                }

                // Save Button
                Button(
                    onClick = {
                        var isValid = true
                        if (title.trim().isEmpty()) {
                            titleError = "Title is required"
                            isValid = false
                        }
                        // Time validation is now handled by the TimePicker, ensuring valid input
                        if (time.trim().isEmpty()) {
                            // If time is empty, it means the user hasn't selected anything
                            // from the picker, so we can consider it an error or default.
                            // For now, let's make it a requirement.
                            isValid = false // Mark invalid if time is still empty
                        }
                        if (location.trim().isEmpty()) {
                            locationError = "Location is required"
                            isValid = false
                        }

                        if (isValid) {
                            val updatedItem = item.copy(
                                title = title.trim(),
                                time = time.trim(),
                                location = location.trim(),
                                description = description.trim(),
                                duration = duration.trim(),
                                type = selectedType,
                                isCompleted = isCompleted
                            )
                            onSave(updatedItem)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TripBookColors.Primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )

    // TimePicker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedHour = timePickerState.hour
                        val selectedMinute = timePickerState.minute
                        val selectedLocalTime = LocalTime.of(selectedHour, selectedMinute)
                        // Format the time to "hh:mm a" (e.g., "01:30 PM")
                        time = selectedLocalTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        showTimePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
