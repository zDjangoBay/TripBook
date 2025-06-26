package com.android.tripbook.ui.uis

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.*
import com.android.tripbook.service.TransportationService
import com.android.tripbook.service.TransportationOption
import com.android.tripbook.service.PassengerInfo
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportationScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onTransportationUpdated: (List<TransportationBooking>) -> Unit,
    transportationService: TransportationService = TransportationService()
) {
    var selectedTab by remember { mutableStateOf("Bookings") }
    var showSearchDialog by remember { mutableStateOf(false) }
    var transportationBookings by remember { mutableStateOf(trip.transportationBookings) }
    
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Transportation",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = trip.name,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
                
                IconButton(
                    onClick = { showSearchDialog = true },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Transportation",
                        tint = Color.White
                    )
                }
            }
            
            // Tab Row
            TabRow(
                selectedTabIndex = if (selectedTab == "Bookings") 0 else 1,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = Color.White.copy(alpha = 0.1f),
                contentColor = Color.White
            ) {
                Tab(
                    selected = selectedTab == "Bookings",
                    onClick = { selectedTab = "Bookings" },
                    text = { Text("My Bookings") }
                )
                Tab(
                    selected = selectedTab == "Regulations",
                    onClick = { selectedTab = "Regulations" },
                    text = { Text("Regulations") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                when (selectedTab) {
                    "Bookings" -> BookingsTab(
                        bookings = transportationBookings,
                        onBookingUpdate = { updatedBookings ->
                            transportationBookings = updatedBookings
                            onTransportationUpdated(updatedBookings)
                        }
                    )
                    "Regulations" -> RegulationsTab()
                }
            }
        }
    }
    
    // Search Dialog
    if (showSearchDialog) {
        TransportationSearchDialog(
            trip = trip,
            transportationService = transportationService,
            onDismiss = { showSearchDialog = false },
            onBookingAdded = { newBooking: TransportationBooking ->
                val updatedBookings = transportationBookings + listOf(newBooking)
                transportationBookings = updatedBookings
                onTransportationUpdated(updatedBookings)
                showSearchDialog = false
            }
        )
    }
}

@Composable
private fun BookingsTab(
    bookings: List<TransportationBooking>,
    onBookingUpdate: (List<TransportationBooking>) -> Unit
) {
    if (bookings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No transportation booked yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tap the + button to search and book transportation",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookings) { booking ->
                TransportationBookingCard(
                    booking = booking,
                    onStatusUpdate = { updatedBooking ->
                        val updatedBookings = bookings.map {
                            if (it.id == updatedBooking.id) updatedBooking else it
                        }
                        onBookingUpdate(updatedBookings)
                    }
                )
            }
        }
    }
}

@Composable
private fun RegulationsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(TransportationType.values()) { type ->
            RegulationCard(type = type)
        }
    }
}

@Composable
private fun TransportationBookingCard(
    booking: TransportationBooking,
    onStatusUpdate: (TransportationBooking) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with type icon and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getTransportationIcon(booking.type),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF667EEA)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.type.name.replace("_", " "),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF667EEA)
                    )
                }
                
                StatusChip(status = booking.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title and Provider
            Text(
                text = booking.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = booking.provider,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Route and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "From",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = booking.departureLocation,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDateTime(booking.departureDateTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "To",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = booking.arrivalLocation,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDateTime(booking.arrivalDateTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Cost and Reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${booking.currency} ${String.format("%.2f", booking.cost)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667EEA)
                )
                
                if (booking.bookingReference.isNotEmpty()) {
                    Text(
                        text = "Ref: ${booking.bookingReference}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

private fun getTransportationIcon(type: TransportationType): ImageVector {
    return when (type) {
        TransportationType.FLIGHT -> Icons.Default.Flight
        TransportationType.TRAIN -> Icons.Default.Train
        TransportationType.BUS -> Icons.Default.DirectionsBus
        TransportationType.CAR_RENTAL -> Icons.Default.DirectionsCar
        TransportationType.TAXI_UBER -> Icons.Default.LocalTaxi
        TransportationType.FERRY -> Icons.Default.DirectionsBoat
        else -> Icons.Default.DirectionsTransit
    }
}

@Composable
private fun StatusChip(status: BookingStatus) {
    val (backgroundColor, textColor) = when (status) {
        BookingStatus.CONFIRMED -> Color(0xFF4CAF50) to Color.White
        BookingStatus.PENDING -> Color(0xFFFF9800) to Color.White
        BookingStatus.CANCELLED -> Color(0xFFF44336) to Color.White
        BookingStatus.COMPLETED -> Color(0xFF2196F3) to Color.White
    }
    
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateTimeString)
        dateTime.format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
    } catch (e: Exception) {
        dateTimeString
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransportationSearchDialog(
    trip: Trip,
    transportationService: TransportationService,
    onDismiss: () -> Unit,
    onBookingAdded: (TransportationBooking) -> Unit
) {
    var selectedType by remember { mutableStateOf(TransportationType.FLIGHT) }
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf("") }
    var passengers by remember { mutableStateOf("1") }
    var searchResults by remember { mutableStateOf<List<TransportationOption>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Transportation") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (!showResults) {
                    // Search Form
                    // Transportation Type Selector
                    Text("Transportation Type", fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TransportationType.values().take(3).forEach { type ->
                            FilterChip(
                                onClick = { selectedType = type },
                                label = { Text(type.name.replace("_", " ")) },
                                selected = selectedType == type,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fromLocation,
                        onValueChange = { fromLocation = it },
                        label = { Text("From") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = toLocation,
                        onValueChange = { toLocation = it },
                        label = { Text("To") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = passengers,
                        onValueChange = { passengers = it },
                        label = { Text("Passengers") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Search Results
                    LazyColumn {
                        items(searchResults) { option ->
                            TransportationOptionCard(
                                option = option,
                                onBook = { selectedOption ->
                                    scope.launch {
                                        val booking = transportationService.bookTransportation(
                                            option = selectedOption,
                                            tripId = trip.id,
                                            passengers = passengers.toIntOrNull() ?: 1,
                                            passengerDetails = emptyList()
                                        )
                                        onBookingAdded(booking)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showResults) {
                Button(
                    onClick = {
                        if (fromLocation.isNotEmpty() && toLocation.isNotEmpty()) {
                            scope.launch {
                                isSearching = true
                                searchResults = transportationService.searchTransportation(
                                    type = selectedType,
                                    from = fromLocation,
                                    to = toLocation,
                                    departureDate = departureDate,
                                    passengers = passengers.toIntOrNull() ?: 1
                                )
                                isSearching = false
                                showResults = true
                            }
                        }
                    },
                    enabled = !isSearching && fromLocation.isNotEmpty() && toLocation.isNotEmpty()
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Search")
                    }
                }
            } else {
                Button(onClick = { showResults = false }) {
                    Text("New Search")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TransportationOptionCard(
    option: TransportationOption,
    onBook: (TransportationOption) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = option.provider,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${option.currency} ${String.format("%.2f", option.price)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667EEA)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${option.departureLocation} → ${option.arrivalLocation}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onBook(option) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Now")
            }
        }
    }
}

@Composable
private fun RegulationCard(type: TransportationType) {
    val transportationService = TransportationService()
    val regulations = transportationService.getTransportationRegulations(type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getTransportationIcon(type),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF667EEA)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = type.name.replace("_", " "),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            regulations.forEach { regulation ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "• ",
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = regulation,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
