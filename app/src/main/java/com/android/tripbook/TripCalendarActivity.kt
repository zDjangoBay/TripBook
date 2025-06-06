package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.data.Trip
import com.android.tripbook.data.TripDatabaseHelper
import com.android.tripbook.ui.theme.TripBookTheme
import java.text.SimpleDateFormat
import java.util.*

class TripCalendarActivity : ComponentActivity() {

    private lateinit var dbHelper: TripDatabaseHelper
    private var selectedDate = mutableStateOf(Date())
    private var allTripsForDate = mutableStateOf(listOf<Trip>())
    private var filterOption = mutableStateOf("All")

    private val addTripLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadTripsForDate()
            Toast.makeText(this, "Trip added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = TripDatabaseHelper(this)
        insertSampleDataIfNeeded()

        setContent {
            TripBookTheme {
                TripCalendarScreen(
                    selectedDate = selectedDate.value,
                    trips = allTripsForDate.value,
                    filterOption = filterOption.value,
                    onDateSelected = { date ->
                        selectedDate.value = date
                        loadTripsForDate()
                    },
                    onFilterChanged = { filter ->
                        filterOption.value = filter
                        applyFilter(filter)
                    },
                    onAddTripClick = {
                        val intent = Intent(this, AddTripActivity::class.java)
                        intent.putExtra("selected_date", selectedDate.value.time)
                        addTripLauncher.launch(intent)
                    }
                )
            }
        }

        // Load initial data
        loadTripsForDate()
    }

    private fun loadTripsForDate() {
        allTripsForDate.value = dbHelper.getTripsByDate(selectedDate.value)
        applyFilter(filterOption.value)
    }

    private fun applyFilter(filter: String) {
        val filteredTrips = when (filter) {
            "All" -> allTripsForDate.value
            "Adventure", "Leisure", "Business" -> allTripsForDate.value.filter { it.type == filter }
            "1 day" -> allTripsForDate.value.filter { it.duration == 1 }
            "3 days" -> allTripsForDate.value.filter { it.duration == 3 }
            "7 days" -> allTripsForDate.value.filter { it.duration == 7 }
            else -> allTripsForDate.value
        }
        // Update the trips list for UI
        allTripsForDate.value = filteredTrips
    }

    private fun insertSampleDataIfNeeded() {
        if (dbHelper.getAllTrips().isNotEmpty()) {
            return
        }

        val calendar = Calendar.getInstance()

        // Sample trips
        val trip1 = Trip(
            title = "Safari Adventure",
            destination = "Serengeti, Tanzania",
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L),
            duration = 7,
            type = "Adventure",
            description = "Amazing wildlife safari experience",
            agency = "African Safari Tours"
        )

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val trip2 = Trip(
            title = "Beach Relaxation",
            destination = "Zanzibar, Tanzania",
            startDate = calendar.time,
            endDate = Date(calendar.timeInMillis + 3 * 24 * 60 * 60 * 1000L),
            duration = 3,
            type = "Leisure",
            description = "Peaceful beach getaway",
            agency = "Island Paradise Tours"
        )

        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val trip3 = Trip(
            title = "Conference Trip",
            destination = "Lagos, Nigeria",
            startDate = calendar.time,
            endDate = Date(calendar.timeInMillis + 1 * 24 * 60 * 60 * 1000L),
            duration = 1,
            type = "Business",
            description = "Tech conference attendance",
            agency = null
        )

        dbHelper.insertTrip(trip1)
        dbHelper.insertTrip(trip2)
        dbHelper.insertTrip(trip3)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCalendarScreen(
    selectedDate: Date,
    trips: List<Trip>,
    filterOption: String,
    onDateSelected: (Date) -> Unit,
    onFilterChanged: (String) -> Unit,
    onAddTripClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var expanded by remember { mutableStateOf(false) }
    val filterOptions =
        listOf("All", "Adventure", "Leisure", "Business", "1 day", "3 days", "7 days")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTripClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Trip"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Trip Calendar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Simple date display (replacing CalendarView for now)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Selected Date",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormat.format(selectedDate),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Filter section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trips for selected date:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = filterOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onFilterChanged(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Trips list
            if (trips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trips found for the selected date",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trips) { trip ->
                        TripItem(trip = trip)
                    }
                }
            }
        }
    }
}

@Composable
fun TripItem(trip: Trip) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = trip.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = trip.destination,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "${dateFormat.format(trip.startDate)} - ${dateFormat.format(trip.endDate)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${trip.duration} days",
                    fontSize = 12.sp
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = trip.type,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
