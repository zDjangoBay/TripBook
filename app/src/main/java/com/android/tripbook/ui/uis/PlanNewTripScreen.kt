package com.android.tripbook.ui.uis



import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanNewTripScreen(
    onBackClick: () -> Unit,
    onTripCreated: (Trip) -> Unit
) {
    var tripName by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var travelers by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedTripType by remember { mutableStateOf("") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Remember date picker states
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    var nameError by remember { mutableStateOf("") }
    var destinationError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var travelersError by remember { mutableStateOf("") }
    var budgetError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Plan New Trip",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Form card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Trip Name
                    Text(
                        text = "Trip Name",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = tripName,
                        onValueChange = {
                            tripName = it
                            nameError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., Egyptian Pyramids Tour") },
                        isError = nameError.isNotEmpty(),
                        supportingText = {
                            if (nameError.isNotEmpty()) {
                                Text(nameError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667EEA),
                            focusedLabelColor = Color(0xFF667EEA),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Destination
                    Text(
                        text = "Destination",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = destination,
                        onValueChange = {
                            destination = it
                            destinationError = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., Cairo, Egypt") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFFE91E63)
                            )
                        },
                        isError = destinationError.isNotEmpty(),
                        supportingText = {
                            if (destinationError.isNotEmpty()) {
                                Text(destinationError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667EEA),
                            focusedLabelColor = Color(0xFF667EEA),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Trip Type Selection
                    Text(
                        text = "Trip Type",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    val tripTypes = listOf("Adventure", "Cultural", "Relaxation", "Safari", "Beach")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tripTypes.take(3).forEach { type ->
                            FilterChip(
                                selected = selectedTripType == type,
                                onClick = { selectedTripType = if (selectedTripType == type) "" else type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF667EEA),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = Color(0xFF64748B)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (selectedTripType == type) Color(0xFF667EEA) else Color(0xFFE2E8F0),
                                    selectedBorderColor = Color(0xFF667EEA)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tripTypes.drop(3).forEach { type ->
                            FilterChip(
                                selected = selectedTripType == type,
                                onClick = { selectedTripType = if (selectedTripType == type) "" else type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF667EEA),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = Color(0xFF64748B)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (selectedTripType == type) Color(0xFF667EEA) else Color(0xFFE2E8F0),
                                    selectedBorderColor = Color(0xFF667EEA)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dates Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Date
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Start Date",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = startDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "",
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStartDatePicker = true },
                                placeholder = { Text("Select date") },
                                readOnly = true,
                                enabled = false,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Date",
                                        tint = Color(0xFF667EEA)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = if (dateError.isNotEmpty()) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                                    disabledTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // End Date
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "End Date",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = endDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "",
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showEndDatePicker = true },
                                placeholder = { Text("Select date") },
                                readOnly = true,
                                enabled = false,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Date",
                                        tint = Color(0xFF667EEA)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = if (dateError.isNotEmpty()) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                                    disabledTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    if (dateError.isNotEmpty()) {
                        Text(
                            text = dateError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Travelers and Budget Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Number of Travelers
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Number of Travelers",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = travelers,
                                onValueChange = {
                                    travelers = it
                                    travelersError = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = "Travelers",
                                        tint = Color(0xFF6B5B95)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = travelersError.isNotEmpty(),
                                supportingText = {
                                    if (travelersError.isNotEmpty()) {
                                        Text(travelersError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF667EEA),
                                    focusedLabelColor = Color(0xFF667EEA),
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        // Budget
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Budget (USD)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = budget,
                                onValueChange = {
                                    budget = it
                                    budgetError = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0") },
                                prefix = { Text("$") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = budgetError.isNotEmpty(),
                                supportingText = {
                                    if (budgetError.isNotEmpty()) {
                                        Text(budgetError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF667EEA),
                                    focusedLabelColor = Color(0xFF667EEA),
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Create Trip Button
                    Button(
                        onClick = {
                            if (validateForm(
                                    tripName,
                                    destination,
                                    startDate,
                                    endDate,
                                    travelers,
                                    budget,
                                    selectedTripType,
                                    setNameError = { nameError = it },
                                    setDestinationError = { destinationError = it },
                                    setDateError = { dateError = it },
                                    setTravelersError = { travelersError = it },
                                    setBudgetError = { budgetError = it }
                                )
                            ) {
                                val newTrip = Trip(
                                    id = System.currentTimeMillis().toString(),
                                    name = tripName.trim(),
                                    startDate = startDate!!,
                                    endDate = endDate!!,
                                    destination = destination.trim(),
                                    travelers = travelers.toInt(),
                                    budget = budget.toInt(),
                                    status = TripStatus.PLANNED,
                                    type = selectedTripType
                                )
                                onTripCreated(newTrip)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667EEA)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Create Trip",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Start Date Picker Dialog
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            startDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            dateError = ""
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = startDatePickerState,
                modifier = Modifier.wrapContentSize()
            )
        }
    }

    // End Date Picker Dialog
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            endDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            dateError = ""
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}

private fun validateForm(
    tripName: String,
    destination: String,
    startDate: LocalDate?,
    endDate: LocalDate?,
    travelers: String,
    budget: String,
    tripType: String,
    setNameError: (String) -> Unit,
    setDestinationError: (String) -> Unit,
    setDateError: (String) -> Unit,
    setTravelersError: (String) -> Unit,
    setBudgetError: (String) -> Unit
): Boolean {
    var isValid = true

    // Validate Trip Name
    if (tripName.trim().isEmpty()) {
        setNameError("Trip name is required")
        isValid = false
    }

    // Validate Destination
    if (destination.trim().isEmpty()) {
        setDestinationError("Destination is required")
        isValid = false
    }

    // Validate Dates
    when {
        startDate == null -> {
            setDateError("Start date is required")
            isValid = false
        }
        endDate == null -> {
            setDateError("End date is required")
            isValid = false
        }
        startDate.isAfter(endDate) -> {
            setDateError("Start date must be before end date")
            isValid = false
        }
    }

    // Validate Travelers
    if (travelers.isEmpty() || travelers.toIntOrNull() == null || travelers.toInt() <= 0) {
        setTravelersError("Enter a valid number of travelers")
        isValid = false
    }

    // Validate Budget
    if (budget.isEmpty() || budget.toIntOrNull() == null || budget.toInt() <= 0) {
        setBudgetError("Enter a valid budget amount")
        isValid = false
    }

    // Validate Trip Type
    if (tripType.isEmpty()) {
        // Note: Fixed this to use a proper trip type error instead of destination error
        setDateError("Please select a trip type")
        isValid = false
    }

    return isValid
}