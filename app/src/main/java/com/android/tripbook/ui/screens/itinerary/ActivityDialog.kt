package com.android.tripbook.ui.screens.itinerary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.data.TripActivity
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDialog(
    activity: TripActivity? = null,
    day: Int = 0,
    tripId: String,
    onDismiss: () -> Unit,
    onSave: (time: String, title: String, location: String) -> Unit
) {
    var time by remember { mutableStateOf(activity?.time ?: "") }
    var title by remember { mutableStateOf(activity?.title ?: "") }
    var location by remember { mutableStateOf(activity?.location ?: "") }
    
    // State for time picker
    var showTimePicker by remember { mutableStateOf(false) }
    
    val isEditing = activity != null
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isEditing) "Edit Activity" else "Add Activity",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time field with time picker
                OutlinedTextField(
                    value = time,
                    onValueChange = { /* Read-only field */ },
                    label = { Text("Time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select time")
                        }
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Activity Title") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { onSave(time, title, location) },
                        enabled = time.isNotBlank() && title.isNotBlank() && location.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
                
                // Time Picker Dialog
                if (showTimePicker) {
                    val timePickerState = rememberTimePickerState()
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        title = { Text("Select Time") },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TimePicker(
                                    state = timePickerState,
                                    layoutType = TimePickerLayoutType.Vertical
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    // Format the time
                                    val hour = timePickerState.hour
                                    val minute = timePickerState.minute
                                    val localTime = LocalTime.of(hour, minute)
                                    
                                    // Format time in 12-hour format with AM/PM
                                    time = localTime.format(DateTimeFormatter.ofPattern("h:mm a"))
                                    
                                    showTimePicker = false
                                }
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}