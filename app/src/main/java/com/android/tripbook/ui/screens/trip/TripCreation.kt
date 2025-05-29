package com.android.tripbook.ui.screens.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.TripRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCreation(
    onNavigateBack: () -> Unit,
    onTripCreated: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var tripName by remember { mutableStateOf("") }
    var tripPurpose by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Trip") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Navigate back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            LinearProgressIndicator(
                progress = (currentStep + 1) / 4f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (currentStep) {
                0 -> DestinationStep(
                    destination = destination,
                    onDestinationChange = { destination = it }
                )
                1 -> DateSelectionStep(
                    startDate = startDate,
                    endDate = endDate,
                    onStartDateChange = { startDate = it },
                    onEndDateChange = { endDate = it }
                )
                2 -> TripDetailsStep(
                    tripName = tripName,
                    tripPurpose = tripPurpose,
                    onTripNameChange = { tripName = it },
                    onTripPurposeChange = { tripPurpose = it }
                )
                3 -> ReviewStep(
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    tripName = tripName,
                    tripPurpose = tripPurpose
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    Button(
                        onClick = { currentStep-- }
                    ) {
                        Text("Previous")
                    }
                }
                
                Button(
                    onClick = {
                        if (currentStep < 3) currentStep++
                        else {
                            // Create a new trip using the repository
                            val tripId = TripRepository.addTrip(
                                destination = destination,
                                startDate = startDate,
                                endDate = endDate,
                                name = tripName,
                                purpose = tripPurpose
                            )
                            onTripCreated(tripId)
                        }
                    },
                    enabled = when (currentStep) {
                        0 -> destination.isNotBlank()
                        1 -> startDate.isNotBlank() && endDate.isNotBlank()
                        2 -> tripName.isNotBlank()
                        else -> true
                    }
                ) {
                    Text(if (currentStep < 3) "Next" else "Create Trip")
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationStep(
    destination: String,
    onDestinationChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Where are you going?",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = destination,
            onValueChange = onDestinationChange,
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionStep(
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "When are you traveling?",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        var showStartDatePicker by remember { mutableStateOf(false) }
        var showEndDatePicker by remember { mutableStateOf(false) }
        
        // Start Date Field
        OutlinedTextField(
            value = startDate,
            onValueChange = { /* Read-only field */ },
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showStartDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // End Date Field
        OutlinedTextField(
            value = endDate,
            onValueChange = { /* Read-only field */ },
            label = { Text("End Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showEndDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                }
            }
        )
        
        // Start Date Picker Dialog
        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                onStartDateChange(date.toString())
                            }
                            showStartDatePicker = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        // End Date Picker Dialog
        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                onEndDateChange(date.toString())
                            }
                            showEndDatePicker = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsStep(
    tripName: String,
    tripPurpose: String,
    onTripNameChange: (String) -> Unit,
    onTripPurposeChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Trip Details",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = tripName,
            onValueChange = onTripNameChange,
            label = { Text("Trip Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = tripPurpose,
            onValueChange = onTripPurposeChange,
            label = { Text("Purpose") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReviewStep(
    destination: String,
    startDate: String,
    endDate: String,
    tripName: String,
    tripPurpose: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Review Trip Details",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        ReviewItem("Destination", destination)
        ReviewItem("Start Date", startDate)
        ReviewItem("End Date", endDate)
        ReviewItem("Trip Name", tripName)
        ReviewItem("Purpose", tripPurpose)
    }
}

@Composable
fun ReviewItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}