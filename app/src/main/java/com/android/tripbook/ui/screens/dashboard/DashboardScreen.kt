package com.android.tripbook.ui.screens.dashboard

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.tripbook.data.providers.DummyTripDataProvider
import com.android.tripbook.data.models.Trip

/**
 * Dashboard screen showing available trips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTripClick: (String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf("") }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val trips = remember { DummyTripDataProvider.getTrips() }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            // Simulate getting current location (in real app, use LocationManager)
            currentLocation = "New York, NY" // Mock location
        }
    }

    // Check location permission on first load
    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            currentLocation = "New York, NY" // Mock location
        }    }    // State for category dropdown visibility and selected category
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }    // Price range state management
    val minPrice = remember { trips.minOfOrNull { it.basePrice } ?: 0.0 }
    val maxPrice = remember { trips.maxOfOrNull { it.basePrice } ?: 5000.0 }
    var priceRangeStart by remember { mutableFloatStateOf(minPrice.toFloat()) }
    var priceRangeEnd by remember { mutableFloatStateOf(maxPrice.toFloat()) }
    var isPriceDialogVisible by remember { mutableStateOf(false) }

    // Duration filter state management
    var selectedDuration by remember { mutableStateOf("All") }
    var isDurationDropdownExpanded by remember { mutableStateOf(false) }
    val availableDurations = remember { 
        listOf("All") + trips.map { it.duration }.distinct().sorted()
    }

// search function
    val filteredTrips = remember(searchQuery, currentLocation, selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration) {
        trips.filter { trip ->
            // Text search filter
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                trip.title.contains(searchQuery, ignoreCase = true) ||
                trip.fromLocation.contains(searchQuery, ignoreCase = true) ||
                trip.toLocation.contains(searchQuery, ignoreCase = true) ||
                (currentLocation.isNotBlank() && trip.fromLocation.contains(currentLocation, ignoreCase = true))
            }
            
            // Category filter
            val matchesCategory = selectedCategory == "All" || 
                trip.category.name.equals(selectedCategory, ignoreCase = true)
            
            // Price range filter
            val matchesPrice = trip.basePrice >= priceRangeStart && trip.basePrice <= priceRangeEnd
            
            // Duration filter
            val matchesDuration = selectedDuration == "All" || 
                trip.duration.equals(selectedDuration, ignoreCase = true)
            
            matchesSearch && matchesCategory && matchesPrice && matchesDuration
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Discover Amazing Trips",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Current Location Display
        if (currentLocation.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Current Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current location: $currentLocation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Enhanced Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search destinations...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (currentLocation.isNotBlank()) {
                    IconButton(
                        onClick = {
                            searchQuery = currentLocation
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Use Current Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),            shape = RoundedCornerShape(16.dp)
        )

        // Filter Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Filter
            FilterChip(
                label = "Category",
                onClick = { isCategoryDropdownExpanded = true }
            )            // Price Range Filter
            FilterChip(
                label = "Price Range",
                onClick = { isPriceDialogVisible = true }
            )            // Duration Filter
            FilterChip(
                label = "Duration",
                onClick = { isDurationDropdownExpanded = true }
            )
        }        // Category Dropdown
        DropdownMenu(
            expanded = isCategoryDropdownExpanded,
            onDismissRequest = { isCategoryDropdownExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            val categories = listOf("All", "Business", "Adventure", "Cultural", "Relaxation", "Family")
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        selectedCategory = category
                        isCategoryDropdownExpanded = false
                    },
                    text = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                )
            }        }

        // Price Range Dialog
        if (isPriceDialogVisible) {
            AlertDialog(
                onDismissRequest = { isPriceDialogVisible = false },
                title = {
                    Text(
                        text = "Price Range",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Select your budget range",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Current range display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$${String.format("%.0f", priceRangeStart)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$${String.format("%.0f", priceRangeEnd)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Range Slider
                        RangeSlider(
                            value = priceRangeStart..priceRangeEnd,
                            onValueChange = { range ->
                                priceRangeStart = range.start
                                priceRangeEnd = range.endInclusive
                            },
                            valueRange = minPrice.toFloat()..maxPrice.toFloat(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Min/Max labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$${String.format("%.0f", minPrice)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$${String.format("%.0f", maxPrice)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { isPriceDialogVisible = false }
                    ) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            // Reset to original range
                            priceRangeStart = minPrice.toFloat()
                            priceRangeEnd = maxPrice.toFloat()
                            isPriceDialogVisible = false
                        }
                    ) {
                        Text("Reset")
                    }
                }            )
        }

        // Duration Dropdown
        DropdownMenu(
            expanded = isDurationDropdownExpanded,
            onDismissRequest = { isDurationDropdownExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            availableDurations.forEach { duration ->
                DropdownMenuItem(
                    onClick = {
                        selectedDuration = duration
                        isDurationDropdownExpanded = false
                    },
                    text = {
                        Text(
                            text = duration,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                )
            }
        }

        // Trip Cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredTrips) { trip ->
                TripCard(
                    trip = trip,
                    onReserveClick = { onTripClick(trip.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    trip: Trip,
    onReserveClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Trip Icon Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (trip.category.name.lowercase()) {
                        "business" -> Icons.Default.Business
                        "adventure" -> Icons.Default.Explore
                        "cultural" -> Icons.Default.Place
                        "relaxation" -> Icons.Default.Spa
                        "family" -> Icons.Default.Groups
                        else -> Icons.Default.Flight
                    },
                    contentDescription = trip.title,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )
            }

            // Trip Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${trip.fromLocation} → ${trip.toLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = trip.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "From $${String.format("%.0f", trip.basePrice)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = onReserveClick,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reserve")
                    }
                }
            }
        }
    }
}

// Helper Composable for Filter Chips
@Composable
fun FilterChip(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
