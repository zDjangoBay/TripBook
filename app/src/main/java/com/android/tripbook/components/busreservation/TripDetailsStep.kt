package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.BusReservation
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsStep(
    reservation: BusReservation,
    cities: List<Pair<String, String>>,
    times: List<String>,
    onReservationUpdate: (BusReservation) -> Unit,
    onContinue: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedFromDropdown by remember { mutableStateOf(false) }
    var expandedToDropdown by remember { mutableStateOf(false) }
    var expandedTimeDropdown by remember { mutableStateOf(false) }
    var expandedPassengerDropdown by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Trip Details",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Select your journey details",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // From City
            ExposedDropdownMenuBox(
                expanded = expandedFromDropdown,
                onExpandedChange = { expandedFromDropdown = !expandedFromDropdown }
            ) {
                OutlinedTextField(
                    value = cities.find { it.first == reservation.from }?.second ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("From") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFromDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFromDropdown,
                    onDismissRequest = { expandedFromDropdown = false }
                ) {
                    cities.forEach { (key, value) ->
                        DropdownMenuItem(
                            text = { Text(value) },
                            onClick = {
                                onReservationUpdate(reservation.copy(from = key))
                                expandedFromDropdown = false
                            }
                        )
                    }
                }
            }
            
            // To City
            ExposedDropdownMenuBox(
                expanded = expandedToDropdown,
                onExpandedChange = { expandedToDropdown = !expandedToDropdown }
            ) {
                OutlinedTextField(
                    value = cities.find { it.first == reservation.to }?.second ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("To") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedToDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedToDropdown,
                    onDismissRequest = { expandedToDropdown = false }
                ) {
                    cities.forEach { (key, value) ->
                        DropdownMenuItem(
                            text = { Text(value) },
                            onClick = {
                                onReservationUpdate(reservation.copy(to = key))
                                expandedToDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Date Picker
            OutlinedTextField(
                value = reservation.date?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Date") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true }
            )
            
            // Time Selection
            ExposedDropdownMenuBox(
                expanded = expandedTimeDropdown,
                onExpandedChange = { expandedTimeDropdown = !expandedTimeDropdown }
            ) {
                OutlinedTextField(
                    value = reservation.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Departure Time") },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTimeDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTimeDropdown,
                    onDismissRequest = { expandedTimeDropdown = false }
                ) {
                    times.forEach { time ->
                        DropdownMenuItem(
                            text = { Text("$time ${if (time.toInt() < 12) "AM" else "PM"}") },
                            onClick = {
                                onReservationUpdate(
                                    reservation.copy(time = LocalTime.parse(time))
                                )
                                expandedTimeDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Passengers
            ExposedDropdownMenuBox(
                expanded = expandedPassengerDropdown,
                onExpandedChange = { expandedPassengerDropdown = !expandedPassengerDropdown }
            ) {
                OutlinedTextField(
                    value = "${reservation.passengers} Passenger${if (reservation.passengers > 1) "s" else ""}",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Passengers") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPassengerDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPassengerDropdown,
                    onDismissRequest = { expandedPassengerDropdown = false }
                ) {
                    (1..4).forEach { count ->
                        DropdownMenuItem(
                            text = { Text("$count Passenger${if (count > 1) "s" else ""}") },
                            onClick = {
                                onReservationUpdate(reservation.copy(passengers = count))
                                expandedPassengerDropdown = false
                            }
                        )
                    }
                }
            }
            
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                enabled = reservation.from.isNotEmpty() && 
                         reservation.to.isNotEmpty() && 
                         reservation.date != null && 
                         reservation.time != null
            ) {
                Text("Continue to Seat Selection")
            }
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                onReservationUpdate(reservation.copy(date = date))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(
                            java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}