package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripStatus
import com.android.tripbook.service.Attraction
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.PlaceResult
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanNewTripScreen(
    onBackClick: () -> Unit,
    onTripCreated: (Trip) -> Unit,
    nominatimService: NominatimService,
    travelAgencyService: TravelAgencyService,
    googleMapsService: GoogleMapsService,
    onBrowseAgencies: (String) -> Unit
) {
    var tripName by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var travelers by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedTripType by remember { mutableStateOf("") }
    var itineraryItems by remember { mutableStateOf<List<ItineraryItem>>(emptyList()) }
    var destinationSuggestions by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var itineraryLocationSuggestions by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isLoadingSuggestions by remember { mutableStateOf(false) }
    var isLoadingItinerarySuggestions by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedItineraryType by remember { mutableStateOf<ItineraryType?>(null) }
    var notes by remember { mutableStateOf("") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showItineraryDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    val itineraryDatePickerState = rememberDatePickerState()

    var nameError by remember { mutableStateOf("") }
    var destinationError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var travelersError by remember { mutableStateOf("") }
    var budgetError by remember { mutableStateOf("") }
    var itineraryDateError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    TripBookGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Header with back button
            TripBookHeader(
                title = "Plan New Trip",
                onBackClick = onBackClick
            )

            // Form card
            TripBookContentCard {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Trip Name
                    TripBookTextField(
                        value = tripName,
                        onValueChange = {
                            tripName = it
                            nameError = ""
                        },
                        label = "Trip Name",
                        placeholder = "e.g., Egyptian Pyramids Tour",
                        isError = nameError.isNotEmpty(),
                        errorMessage = nameError
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Destination
                    TripBookSectionTitle("Destination")
                    Column {
                        OutlinedTextField(
                            value = destination,
                            onValueChange = {
                                destination = it
                                destinationError = ""
                                if (it.trim().length >= 3) {
                                    isLoadingSuggestions = true
                                    coroutineScope.launch {
                                        try {
                                            // Try Google Places API first
                                            destinationSuggestions = googleMapsService.searchPlaces(it)
                                            isLoadingSuggestions = false
                                        } catch (e: Exception) {
                                            // Fallback to Nominatim
                                            try {
                                                val attractions = nominatimService.getNearbyAttractions(it)
                                                destinationSuggestions = attractions.map { attraction ->
                                                    PlaceResult(
                                                        placeId = "",
                                                        name = attraction.name,
                                                        address = attraction.location,
                                                        types = listOf("tourist_attraction")
                                                    )
                                                }
                                                isLoadingSuggestions = false
                                            } catch (e2: Exception) {
                                                destinationSuggestions = emptyList()
                                                isLoadingSuggestions = false
                                            }
                                        }
                                    }
                                } else {
                                    destinationSuggestions = emptyList()
                                    isLoadingSuggestions = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g., Cairo, Egypt") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = TripBookColors.Secondary
                                )
                            },
                            isError = destinationError.isNotEmpty(),
                            supportingText = {
                                if (destinationError.isNotEmpty()) {
                                    Text(destinationError, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TripBookColors.BorderFocused,
                                focusedLabelColor = TripBookColors.BorderFocused,
                                unfocusedBorderColor = TripBookColors.Border
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
                        } else if (destinationSuggestions.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 200.dp)
                                ) {
                                    items(destinationSuggestions) { suggestion ->
                                        EnhancedSuggestionItem(
                                            suggestion = suggestion,
                                            onClick = {
                                                destination = "${suggestion.name}, ${suggestion.address}"
                                                destinationSuggestions = emptyList()
                                                if (startDate != null) {
                                                    itineraryItems = itineraryItems + ItineraryItem(
                                                        date = startDate!!,
                                                        time = "10:00 AM",
                                                        title = suggestion.name,
                                                        location = suggestion.address,
                                                        type = ItineraryType.ACTIVITY,
                                                        notes = "Suggested attraction"
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Browse Travel Agencies Button
                    if (destination.isNotEmpty()) {
                        TripBookSecondaryButton(
                            text = "Browse Travel Agencies",
                            onClick = { onBrowseAgencies(destination) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Trip Type Selection
                    TripBookSectionTitle("Trip Type")
                    val tripTypes = listOf("Adventure", "Cultural", "Relaxation", "Safari", "Beach")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tripTypes.take(3).forEach { type ->
                            TripBookFilterChip(
                                selected = selectedTripType == type,
                                onClick = { selectedTripType = if (selectedTripType == type) "" else type },
                                label = type
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
                            TripBookFilterChip(
                                selected = selectedTripType == type,
                                onClick = { selectedTripType = if (selectedTripType == type) "" else type },
                                label = type
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
                                text = "Budget (FCFA)",
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
                                prefix = { Text("FCFA") },
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

                    Spacer(modifier = Modifier.height(20.dp))

                    // Itinerary Builder Section
                    Text(
                        text = "Itinerary (Optional)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Date
                    Text(
                        text = "Date",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = date?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showItineraryDatePicker = true },
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
                        isError = itineraryDateError.isNotEmpty(),
                        supportingText = {
                            if (itineraryDateError.isNotEmpty()) {
                                Text(itineraryDateError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = if (itineraryDateError.isNotEmpty()) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                            disabledTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time
                    Text(
                        text = "Time",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = "Title",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location
                    Text(
                        text = "Location",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
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
                                    isLoadingItinerarySuggestions = true
                                    coroutineScope.launch {
                                        try {
                                            itineraryLocationSuggestions = googleMapsService.searchPlaces(it)
                                            isLoadingItinerarySuggestions = false
                                        } catch (e: Exception) {
                                            try {
                                                val attractions = nominatimService.getNearbyAttractions(it)
                                                itineraryLocationSuggestions = attractions.map { attraction ->
                                                    PlaceResult(
                                                        placeId = "",
                                                        name = attraction.name,
                                                        address = attraction.location,
                                                        types = listOf("tourist_attraction")
                                                    )
                                                }
                                                isLoadingItinerarySuggestions = false
                                            } catch (e2: Exception) {
                                                itineraryLocationSuggestions = emptyList()
                                                isLoadingItinerarySuggestions = false
                                            }
                                        }
                                    }
                                } else {
                                    itineraryLocationSuggestions = emptyList()
                                    isLoadingItinerarySuggestions = false
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

                        // Location suggestions for itinerary
                        if (isLoadingItinerarySuggestions) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp),
                                strokeWidth = 2.dp
                            )
                        } else if (itineraryLocationSuggestions.isNotEmpty()) {
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
                                    items(itineraryLocationSuggestions) { suggestion ->
                                        EnhancedSuggestionItem(
                                            suggestion = suggestion,
                                            onClick = {
                                                location = "${suggestion.name}, ${suggestion.address}"
                                                itineraryLocationSuggestions = emptyList()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Itinerary Type
                    Text(
                        text = "Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
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
                            FilterChip(
                                selected = selectedItineraryType == type,
                                onClick = { selectedItineraryType = if (selectedItineraryType == type) null else type },
                                label = { Text(type.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF667EEA),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = Color(0xFF64748B)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedItineraryType == type,
                                    borderColor = if (selectedItineraryType == type) Color(0xFF667EEA) else Color(0xFFE2E8F0),
                                    selectedBorderColor = Color(0xFF667EEA)
                                ),
                                shape = RoundedCornerShape(16.dp)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes
                    Text(
                        text = "Notes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("Additional details or notes") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667EEA),
                            focusedLabelColor = Color(0xFF667EEA),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add Itinerary Item Button
                    Button(
                        onClick = {
                            if (validateItineraryItem(
                                    date,
                                    time,
                                    title,
                                    location,
                                    selectedItineraryType,
                                    setDateError = { itineraryDateError = it },
                                    setTimeError = { timeError = it },
                                    setTitleError = { titleError = it },
                                    setLocationError = { locationError = it },
                                    setTypeError = { typeError = it }
                                )
                            ) {
                                val newItem = ItineraryItem(
                                    tripId = "", // Will be set when trip is created
                                    date = date!!,
                                    time = time.trim(),
                                    title = title.trim(),
                                    location = location.trim(),
                                    type = selectedItineraryType!!,
                                    notes = notes.trim()
                                )
                                itineraryItems = itineraryItems + newItem
                                // Reset itinerary form
                                date = null
                                time = ""
                                title = ""
                                location = ""
                                selectedItineraryType = null
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
                                    budget = budget.toDoubleOrNull() ?: 0.0,
                                    category = TripCategory.valueOf(selectedTripType.uppercase()),
                                    status = TripStatus.PLANNED,
                                    itinerary = itineraryItems
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

    // Itinerary Date Picker Dialog
    if (showItineraryDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showItineraryDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        itineraryDatePickerState.selectedDateMillis?.let { millis ->
                            date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            itineraryDateError = ""
                        }
                        showItineraryDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showItineraryDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = itineraryDatePickerState,
                modifier = Modifier.wrapContentSize()
            )
        }
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
private fun EnhancedSuggestionItem(suggestion: PlaceResult, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Place",
                tint = Color(0xFF667EEA),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suggestion.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A202C)
                )
                Text(
                    text = suggestion.address,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
            }
        }

        // Display place types as chips
        if (suggestion.types.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                suggestion.types.take(3).forEach { type ->
                    Surface(
                        color = Color(0xFFE3F2FD),
                        contentColor = Color(0xFF1976D2),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text(
                            text = type.replace("_", " ").lowercase().split(" ")
                                .joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } },
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
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

    if (tripName.trim().isEmpty()) {
        setNameError("Trip name is required")
        isValid = false
    }

    if (destination.trim().isEmpty()) {
        setDestinationError("Destination is required")
        isValid = false
    }

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

    if (travelers.isEmpty() || travelers.toIntOrNull() == null || travelers.toInt() <= 0) {
        setTravelersError("Enter a valid number of travelers")
        isValid = false
    }

    if (budget.isEmpty() || budget.toIntOrNull() == null || budget.toInt() <= 0) {
        setBudgetError("Enter a valid budget amount")
        isValid = false
    }

    if (tripType.isEmpty()) {
        setDateError("Please select a trip type")
        isValid = false
    }

    return isValid
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
