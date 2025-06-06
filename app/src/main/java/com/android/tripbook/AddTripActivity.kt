package com.android.tripbook

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.data.Trip
import com.android.tripbook.data.TripDatabaseHelper
import com.android.tripbook.ui.theme.TripBookTheme
import java.text.SimpleDateFormat
import java.util.*

class AddTripActivity : ComponentActivity() {

    private lateinit var dbHelper: TripDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = TripDatabaseHelper(this)

        // Get preselected date from intent if available
        val preselectedDateMillis = intent.getLongExtra("selected_date", -1)
        val preselectedDate =
            if (preselectedDateMillis != -1L) Date(preselectedDateMillis) else null

        setContent {
            TripBookTheme {
                AddTripScreen(
                    preselectedDate = preselectedDate,
                    onSaveTrip = { trip ->
                        val result = dbHelper.insertTrip(trip)
                        if (result != -1L) {
                            Toast.makeText(
                                this@AddTripActivity,
                                "Trip saved successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddTripActivity,
                                "Error saving trip",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onCancel = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(
    preselectedDate: Date?,
    onSaveTrip: (Trip) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    var tripTitle by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(preselectedDate) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var selectedTripType by remember { mutableStateOf("Adventure") }
    var agency by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val tripTypes = listOf("Adventure", "Leisure", "Business", "Cultural", "Medical", "Educational")

    // Auto-set end date to same day if start date is set and end date is not
    LaunchedEffect(startDate) {
        if (startDate != null && endDate == null) {
            endDate = startDate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add New Trip",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tripTitle,
            onValueChange = { tripTitle = it },
            label = { Text("Trip Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Column {
            Text(
                text = "Start Date",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    showDatePicker(context) { selectedDate ->
                        startDate = selectedDate
                        if (endDate == null) {
                            endDate = selectedDate
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = startDate?.let { dateFormat.format(it) } ?: "Select Start Date",
                    fontSize = 16.sp
                )
            }
        }

        Column {
            Text(
                text = "End Date",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    showDatePicker(context) { selectedDate ->
                        if (startDate != null && selectedDate.before(startDate)) {
                            Toast.makeText(
                                context,
                                "End date cannot be before start date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            endDate = selectedDate
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = endDate?.let { dateFormat.format(it) } ?: "Select End Date",
                    fontSize = 16.sp
                )
            }
        }

        Column {
            Text(
                text = "Trip Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedTripType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tripTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedTripType = type
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = agency,
            onValueChange = { agency = it },
            label = { Text("Travel Agency (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", fontSize = 16.sp)
            }

            Button(
                onClick = {
                    validateAndSave(
                        tripTitle = tripTitle,
                        destination = destination,
                        startDate = startDate,
                        endDate = endDate,
                        selectedTripType = selectedTripType,
                        agency = agency,
                        description = description,
                        onSaveTrip = onSaveTrip,
                        context = context
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Trip", fontSize = 16.sp)
            }
        }
    }
}

private fun showDatePicker(
    context: android.content.Context,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun validateAndSave(
    tripTitle: String,
    destination: String,
    startDate: Date?,
    endDate: Date?,
    selectedTripType: String,
    agency: String,
    description: String,
    onSaveTrip: (Trip) -> Unit,
    context: android.content.Context
) {
    if (tripTitle.trim().isEmpty()) {
        Toast.makeText(context, "Trip title is required", Toast.LENGTH_SHORT).show()
        return
    }

    if (destination.trim().isEmpty()) {
        Toast.makeText(context, "Destination is required", Toast.LENGTH_SHORT).show()
        return
    }

    if (startDate == null) {
        Toast.makeText(context, "Please select start date", Toast.LENGTH_SHORT).show()
        return
    }

    if (endDate == null) {
        Toast.makeText(context, "Please select end date", Toast.LENGTH_SHORT).show()
        return
    }

    // Calculate duration
    val diffInMillis = endDate.time - startDate.time
    val duration = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1

    // Create trip object
    val trip = Trip(
        title = tripTitle.trim(),
        destination = destination.trim(),
        startDate = startDate,
        endDate = endDate,
        duration = duration,
        type = selectedTripType,
        description = description.trim().takeIf { it.isNotEmpty() },
        agency = agency.trim().takeIf { it.isNotEmpty() }
    )

    onSaveTrip(trip)
}
