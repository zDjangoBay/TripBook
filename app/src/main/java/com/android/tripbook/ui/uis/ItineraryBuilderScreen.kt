package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.android.tripbook.ui.components.*
import com.android.tripbook.ui.theme.TripBookColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Trip
import com.android.tripbook.service.Attraction
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.SnackbarHostState
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.CalendarToday
import com.android.tripbook.service.CalendarIntegrationService





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryBuilderScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onItineraryUpdated: (List<ItineraryItem>) -> Unit,
    nominatimService: NominatimService,
    travelAgencyService: TravelAgencyService,
    onBrowseAgencies: (String) -> Unit
) {
    val scaffoldState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ItineraryType?>(null) }
    var notes by remember { mutableStateOf("") }
    var locationSuggestions by remember { mutableStateOf<List<Attraction>>(emptyList()) }
    var isLoadingSuggestions by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var dateError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }

    var itineraryItems by remember { mutableStateOf(trip.itinerary) }

    val coroutineScope = rememberCoroutineScope()

    var showCalendarPicker by remember { mutableStateOf(false) }
    var selectedCalendarId by remember { mutableStateOf<Long?>(null) }

    TripBookGradientBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = scaffoldState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp)
                    .padding(paddingValues)
            ) {
                // Header with back button
                TripBookHeader(
                    title = "Build Itinerary: ${trip.name}",
                    onBackClick = onBackClick
                )

                // Form card
                TripBookContentCard {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Browse Travel Agencies Button
                        TripBookSecondaryButton(
                            text = "Browse Travel Agencies",
                            onClick = { onBrowseAgencies(trip.destination) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Date
                        Text(
                            text = "Date",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = date?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true },
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
                            isError = dateError.isNotEmpty(),
                            supportingText = {
                                if (dateError.isNotEmpty()) {
                                    Text(dateError, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = if (dateError.isNotEmpty()) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                                disabledTextColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Time
                        Text(
                            text = "Time",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = {
                                time = it
                                timeError = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g., 10:00 AM") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Time",
                                    tint = Color(0xFF667EEA)
                                )
                            },
                            isError = timeError.isNotEmpty(),
                            supportingText = {
                                if (timeError.isNotEmpty()) {
                                    Text(timeError, color = MaterialTheme.colorScheme.error)
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

                        // Title
                        Text(
                            text = "Title",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                titleError = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g., Visit Pyramids") },
                            isError = titleError.isNotEmpty(),
                            supportingText = {
                                if (titleError.isNotEmpty()) {
                                    Text(titleError, color = MaterialTheme.colorScheme.error)
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

                        // Location
                        Text(
                            text = "Location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column {
                            OutlinedTextField(
                                value = location,
                                onValueChange = {
                                    location = it
                                    locationError = ""
                                    if (it.trim().length >= 3) {
                                        isLoadingSuggestions = true
                                        coroutineScope.launch {
                                            locationSuggestions = nominatimService.getNearbyAttractions(it)
                                            isLoadingSuggestions = false
                                        }
                                    } else {
                                        locationSuggestions = emptyList()
                                        isLoadingSuggestions = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g., Giza, Egypt") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = Color(0xFFE91E63)
                                    )
                                },
                                isError = locationError.isNotEmpty(),
                                supportingText = {
                                    if (locationError.isNotEmpty()) {
                                        Text(locationError, color = MaterialTheme.colorScheme.error)
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
                            if (isLoadingSuggestions) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(top = 8.dp)
                                )
                            } else if (locationSuggestions.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 150.dp)
                                    ) {
                                        items(locationSuggestions) { suggestion ->
                                            SuggestionItem(
                                                suggestion = suggestion,
                                                onClick = {
                                                    title = suggestion.name
                                                    location = suggestion.location
                                                    selectedType = ItineraryType.ACTIVITY
                                                    locationSuggestions = emptyList()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Itinerary Type
                        Text(
                            text = "Type",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ItineraryType.values().forEach { type ->
                                TripBookFilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = if (selectedType == type) null else type },
                                    label = type.name
                                )
                            }
                        }
                        if (typeError.isNotEmpty()) {
                            Text(
                                text = typeError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Notes
                        Text(
                            text = "Notes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { Text("Additional details or notes") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667EEA),
                                focusedLabelColor = Color(0xFF667EEA),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Add Item Button
                        Button(
                            onClick = {
                                if (validateItineraryItem(
                                        date,
                                        time,
                                        title,
                                        location,
                                        selectedType,
                                        setDateError = { dateError = it },
                                        setTimeError = { timeError = it },
                                        setTitleError = { titleError = it },
                                        setLocationError = { locationError = it },
                                        setTypeError = { typeError = it }
                                    )
                                ) {
                                    val newItem = ItineraryItem(
                                        tripId = trip.id,
                                        date = date!!,
                                        time = time.trim(),
                                        title = title.trim(),
                                        location = location.trim(),
                                        type = selectedType!!,
                                        notes = notes.trim()
                                    )
                                    itineraryItems = itineraryItems + newItem
                                    // Reset form
                                    date = null
                                    time = ""
                                    title = ""
                                    location = ""
                                    selectedType = null
                                    notes = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Add Itinerary Item",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Itinerary List
                        if (itineraryItems.isNotEmpty()) {
                            Text(
                                text = "Itinerary Items",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(itineraryItems) { item ->
                                    ItineraryItemCard(item = item)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Save Itinerary Button
                        Button(
                            onClick = {
                                onItineraryUpdated(itineraryItems)
                                onBackClick()
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
                                text = "Save Itinerary",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        // Calendar Sync Button
                        Button(
                            onClick = {
                                if (!CalendarIntegrationService.hasCalendarPermission(context)) {
                                    // Show rationale and request permission
                                    // (You may need to pass the Activity context for request)
                                } else {
                                    showCalendarPicker = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sync to Calendar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            dateError = ""
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.wrapContentSize()
            )
        }
    }

    if (showCalendarPicker) {
        CalendarPickerDialog(
            context = context,
            onCalendarSelected = { calendarId ->
                selectedCalendarId = calendarId
                val results = CalendarIntegrationService.syncTripToCalendar(
                    context,
                    itineraryItems,
                    calendarId
                )
                coroutineScope.launch {
                    scaffoldState.showSnackbar(
                        if (results.any { it != null }) "Trip synced to calendar"
                        else "Trip already synced"
                    )
                }
                showCalendarPicker = false
            },
            onDismiss = { showCalendarPicker = false }
        )
    }

    itineraryItems.forEach { item ->
        val isSynced = selectedCalendarId?.let { CalendarIntegrationService.isEventAlreadySynced(context, item, it) } ?: false
        CalendarSyncButton(
            isSynced = isSynced,
            onSync = {
                if (selectedCalendarId != null) {
                    CalendarIntegrationService.insertEvent(context, item, selectedCalendarId!!)
                } else {
                    showCalendarPicker = true
                }
            }
        )
    }
}

@Composable
private fun ItineraryItemCard(item: ItineraryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "${item.date.format(DateTimeFormatter.ofPattern("MMM d"))} - ${item.time}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF667EEA)
            )
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = item.location,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = item.type.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = when (item.type) {
                    ItineraryType.ACTIVITY -> Color(0xFF667EEA)
                    ItineraryType.ACCOMMODATION -> Color(0xFFE91E63)
                    ItineraryType.TRANSPORTATION -> Color(0xFF00CC66)
                }
            )
            if (item.agencyService != null) {
                Text(
                    text = "Booked via: ${item.agencyService.name} ($${item.agencyService.price})",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (item.notes.isNotEmpty()) {
                Text(
                    text = "Notes: ${item.notes}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: Attraction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Attraction",
            tint = Color(0xFF667EEA),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = suggestion.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A202C)
            )
            Text(
                text = suggestion.location,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

private fun validateItineraryItem(
    date: LocalDate?,
    time: String,
    title: String,
    location: String,
    type: ItineraryType?,
    setDateError: (String) -> Unit,
    setTimeError: (String) -> Unit,
    setTitleError: (String) -> Unit,
    setLocationError: (String) -> Unit,
    setTypeError: (String) -> Unit
): Boolean {
    var isValid = true

    if (date == null) {
        setDateError("Date is required")
        isValid = false
    }

    if (time.trim().isEmpty()) {
        setTimeError("Time is required")
        isValid = false
    } else if (!time.matches(Regex("^(1[0-2]|0?[1-9]):[0-5][0-9] ?([AP]M)$"))) {
        setTimeError("Enter valid time (e.g., 10:00 AM)")
        isValid = false
    }

    if (title.trim().isEmpty()) {
        setTitleError("Title is required")
        isValid = false
    }

    if (location.trim().isEmpty()) {
        setLocationError("Location is required")
        isValid = false
    }

    if (type == null) {
        setTypeError("Please select an itinerary type")
        isValid = false
    }

    return isValid
}

@Composable
fun CalendarPickerDialog(
    context: Context,
    onCalendarSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendars = remember { CalendarIntegrationService.getCalendars(context) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Calendar") },
        text = {
            Column {
                calendars.forEach { (id, name) ->
                    TextButton(onClick = { onCalendarSelected(id) }) {
                        Text(name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CalendarSyncButton(
    isSynced: Boolean,
    onSync: () -> Unit
) {
    Button(onClick = onSync) {
        if (isSynced) {
            Icon(Icons.Default.Check, contentDescription = "Synced")
        } else {
            Icon(Icons.Default.CalendarToday, contentDescription = "Sync")
        }
        Spacer(Modifier.width(8.dp))
        Text(if (isSynced) "Synced" else "Sync to Calendar")
    }
}