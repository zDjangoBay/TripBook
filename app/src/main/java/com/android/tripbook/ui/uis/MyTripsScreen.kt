package com.android.tripbook.ui.uis


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions // This was likely already there
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // <--- Make sure this line is present
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import com.android.tripbook.viewmodel.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.android.tripbook.R
@OptIn(ExperimentalMaterial3Api::class) // Added for AlertDialog
@Composable
fun MyTripsScreen(
    tripViewModel: TripViewModel = viewModel(),
    onPlanNewTripClick: () -> Unit,
    onTripClick: ((Trip) -> Unit)? = null,
    onAgenciesClick: () -> Unit
) {
    val trips by tripViewModel.trips.collectAsState()
    val isLoading by tripViewModel.isLoading.collectAsState()
    val error by tripViewModel.error.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }

    // State for showing the delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    // State for showing the edit dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var tripToEdit by remember { mutableStateOf<Trip?>(null) }
    var editedTripName by remember { mutableStateOf("") }
    var editedTripDestination by remember { mutableStateOf("") }
    var editedTripStartDate by remember { mutableStateOf(LocalDate.now()) }
    var editedTripEndDate by remember { mutableStateOf(LocalDate.now()) }
    var editedTripTravelers by remember { mutableStateOf("") }
    var editedTripBudget by remember { mutableStateOf("") }


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
                .padding(20.dp)
        ) {
            // Header with Agencies Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Trips",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                IconButton(
                    onClick = onAgenciesClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Travel Agencies",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = "Plan your African adventure",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Search trips...",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Planned", "Active", "Completed").forEach { tab ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedTab == tab) Color.White else Color.Transparent,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedTab == tab) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == tab) Color(0xFF667EEA) else Color.White
                        )
                    }
                }
            }

            // Trip List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trips.filter { trip ->
                    (selectedTab == "All" ||
                            (selectedTab == "Planned" && trip.status == TripStatus.PLANNED) ||
                            (selectedTab == "Active" && trip.status == TripStatus.ACTIVE) ||
                            (selectedTab == "Completed" && trip.status == TripStatus.COMPLETED)) &&
                            (searchText.isEmpty() ||
                                    trip.name.contains(searchText, ignoreCase = true) ||
                                    trip.destination.contains(searchText, ignoreCase = true))
                }) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onTripClick?.invoke(trip) },
                        onEditClick = {
                            tripToEdit = it
                            editedTripName = it.name
                            editedTripDestination = it.destination
                            editedTripStartDate = it.startDate
                            editedTripEndDate = it.endDate
                            editedTripTravelers = it.travelers.toString()
                            editedTripBudget = it.budget.toString()
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            tripToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onPlanNewTripClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color.White,
            contentColor = Color(0xFF667EEA),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Trip",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Trip") },
            text = { Text("Are you sure you want to delete '${tripToDelete?.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        tripToDelete?.let {
                            tripViewModel.deleteTrip(it.id)
                        }
                        showDeleteDialog = false
                        tripToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        tripToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Trip Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Trip") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedTripName,
                        onValueChange = { editedTripName = it },
                        label = { Text("Trip Name") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedTripDestination,
                        onValueChange = { editedTripDestination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    // You'll need to implement date pickers for startDate and endDate
                    // For simplicity, I'm keeping them as text fields for now, but a proper UI for date selection is recommended.
                    OutlinedTextField(
                        value = editedTripStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        onValueChange = {
                            try {
                                editedTripStartDate = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            } catch (e: Exception) {
                                // Handle invalid date format
                            }
                        },
                        label = { Text("Start Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedTripEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        onValueChange = {
                            try {
                                editedTripEndDate = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            } catch (e: Exception) {
                                // Handle invalid date format
                            }
                        },
                        label = { Text("End Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedTripTravelers,
                        onValueChange = { editedTripTravelers = it },
                        label = { Text("Travelers") },
                        // Ensure this line uses 'KeyboardType.Number'
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedTripTravelers,
                        onValueChange = { editedTripTravelers = it },
                        label = { Text("Travelers") },
                        // Ensure this line uses 'KeyboardType.Number'
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        tripToEdit?.let {
                            val updatedTrip = it.copy(
                                name = editedTripName,
                                destination = editedTripDestination,
                                startDate = editedTripStartDate,
                                endDate = editedTripEndDate,
                                travelers = editedTripTravelers.toIntOrNull() ?: it.travelers,
                                budget = editedTripBudget.toIntOrNull() ?: it.budget
                            )
                            tripViewModel.updateTrip(updatedTrip)
                        }
                        showEditDialog = false
                        tripToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showEditDialog = false
                        tripToEdit = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    onEditClick: (Trip) -> Unit, // New parameter for edit button
    onDeleteClick: (Trip) -> Unit // New parameter for delete button
) {
    val statusColor = when (trip.status) {
        TripStatus.PLANNED -> Color(0xFF0066CC)
        TripStatus.ACTIVE -> Color(0xFF00CC66)
        TripStatus.COMPLETED -> Color(0xFF666666)
    }
    val statusBgColor = when (trip.status) {
        TripStatus.PLANNED -> Color(0xFFE6F3FF)
        TripStatus.ACTIVE -> Color(0xFFE6FFE6)
        TripStatus.COMPLETED -> Color(0xFFF0F0F0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                    Text(
                        text = "${trip.startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${trip.endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                        fontSize = 14.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Edit Button
                    IconButton(
                        onClick = { onEditClick(trip) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Trip",
                            tint = Color(0xFF64748B)
                        )
                    }
                    // Delete Button
                    IconButton(
                        onClick = { onDeleteClick(trip) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Trip",
                            tint = Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // Add some space between buttons and status
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusBgColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = trip.status.name.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "📍",
                        fontSize = 14.sp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trip.destination,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                // Travelers
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "👥",
                        fontSize = 14.sp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${trip.travelers} travelers",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                // Budget
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "💰",
                        fontSize = 14.sp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$${trip.budget}",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}