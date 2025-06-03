package com.android.tripbook.ui.screens.dashboard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.tripbook.data.providers.DummyTripDataProvider
import com.android.tripbook.data.models.Trip
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

/**
 * Dashboard screen showing available trips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTripClick: (String) -> Unit
) {    val context = LocalContext.current
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
            )        } else {
            currentLocation = "New York, NY" // Mock location
        }
    }

    // Filter preferences helper functions
    val prefs = remember { context.getSharedPreferences("trip_filters", Context.MODE_PRIVATE) }
      fun saveFilterPreferences(
        category: String,
        priceStart: Float,
        priceEnd: Float,
        duration: String,
        search: String
    ) {
        prefs.edit().apply {
            putString("selected_category", category)
            putFloat("price_range_start", priceStart)
            putFloat("price_range_end", priceEnd)
            putString("selected_duration", duration)
            putString("search_query", search)
            apply()
        }
    }

    fun clearAllFilterPreferences() {
        prefs.edit().apply {
            remove("selected_category")
            remove("price_range_start")
            remove("price_range_end")
            remove("selected_duration")
            remove("search_query")
            apply()
        }
    }

    // State for category dropdown visibility and selected category with persistence
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { 
        mutableStateOf(prefs.getString("selected_category", "All") ?: "All") 
    }
      // Price range state management with persistence
    val minPrice = remember { trips.minOfOrNull { it.basePrice } ?: 0.0 }
    val maxPrice = remember { trips.maxOfOrNull { it.basePrice } ?: 5000.0 }
    var priceRangeStart by remember { 
        val savedStart = prefs.getFloat("price_range_start", minPrice.toFloat())
        // Validate saved value is within bounds
        mutableFloatStateOf(savedStart.coerceIn(minPrice.toFloat(), maxPrice.toFloat()))
    }
    var priceRangeEnd by remember { 
        val savedEnd = prefs.getFloat("price_range_end", maxPrice.toFloat())
        // Validate saved value is within bounds
        mutableFloatStateOf(savedEnd.coerceIn(minPrice.toFloat(), maxPrice.toFloat()))
    }
    var isPriceDialogVisible by remember { mutableStateOf(false) }    // Duration filter state management with persistence
    val availableDurations = remember { 
        listOf("All") + trips.map { it.duration }.distinct().sorted()
    }
    var selectedDuration by remember { 
        val savedDuration = prefs.getString("selected_duration", "All") ?: "All"
        // Validate saved duration is still available
        mutableStateOf(if (availableDurations.contains(savedDuration)) savedDuration else "All")
    }
    var isDurationDropdownExpanded by remember { mutableStateOf(false) }    // Search query with persistence and advanced debouncing
    var searchQuery by remember { 
        mutableStateOf(prefs.getString("search_query", "") ?: "") 
    }
    
    // Debounced search query for performance
    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }
    
    // Advanced debounce search input with job cancellation for better performance
    LaunchedEffect(searchQuery) {
        val searchJob = Job()
        try {
            delay(300) // 300ms debounce delay
            if (searchJob.isActive) {
                debouncedSearchQuery = searchQuery
            }
        } catch (e: Exception) {
            // Handle cancellation gracefully
        }
    }    // Save preferences whenever filter state changes (with debounced search and throttling)
    LaunchedEffect(selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration, debouncedSearchQuery) {
        // Throttle preference saves to avoid excessive I/O
        delay(100)
        saveFilterPreferences(selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration, debouncedSearchQuery)
    }

    // Memoized category names for performance
    val categoryNames = remember {
        mapOf(
            "business" to "Business",
            "adventure" to "Adventure", 
            "cultural" to "Cultural",
            "relaxation" to "Relaxation",
            "family" to "Family"
        )
    }    // High-performance optimized search function with advanced filtering
    val filteredTrips = remember(debouncedSearchQuery, currentLocation, selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration) {
        // Early return if no trips available
        if (trips.isEmpty()) return@remember emptyList<Trip>()
        
        // Pre-compute search terms for better performance
        val searchTerms = if (debouncedSearchQuery.isBlank()) {
            emptyList()
        } else {
            debouncedSearchQuery.lowercase().trim()
                .split(Regex("\\s+")) // Split on any whitespace
                .filter { it.isNotBlank() && it.length >= 2 } // Filter short terms for better relevance
        }
        
        val currentLocationLower = currentLocation.lowercase()
        val hasLocationFilter = currentLocationLower.isNotBlank()
        
        // Pre-compute filter states for performance
        val hasSearchFilter = searchTerms.isNotEmpty()
        val hasCategoryFilter = selectedCategory != "All"
        val hasPriceFilter = priceRangeStart != minPrice.toFloat() || priceRangeEnd != maxPrice.toFloat()
        val hasDurationFilter = selectedDuration != "All"
        
        trips.asSequence() // Use sequence for lazy evaluation
            .filter { trip ->
                // Category filter first (most selective)
                if (hasCategoryFilter && !trip.category.name.equals(selectedCategory, ignoreCase = true)) {
                    return@filter false
                }
                
                // Price range filter (numeric comparison is fast)
                if (hasPriceFilter && trip.basePrice !in priceRangeStart..priceRangeEnd) {
                    return@filter false
                }
                
                // Duration filter (simple string comparison)
                if (hasDurationFilter && !trip.duration.equals(selectedDuration, ignoreCase = true)) {
                    return@filter false
                }
                
                // Text search filter (most expensive, so do it last)
                if (hasSearchFilter) {
                    val tripTitle = trip.title.lowercase()
                    val fromLocation = trip.fromLocation.lowercase()
                    val toLocation = trip.toLocation.lowercase()
                    
                    val matchesSearchTerms = searchTerms.any { term ->
                        tripTitle.contains(term) ||
                        fromLocation.contains(term) ||
                        toLocation.contains(term)
                    }
                    
                    val matchesCurrentLocation = hasLocationFilter && fromLocation.contains(currentLocationLower)
                    
                    if (!matchesSearchTerms && !matchesCurrentLocation) {
                        return@filter false
                    }
                }
                
                true
            }
            .toList() // Convert sequence back to list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {        // Header with filter count (using debounced search for consistency)
        val activeFilterCount = listOf(
            selectedCategory != "All",
            priceRangeStart != minPrice.toFloat() || priceRangeEnd != maxPrice.toFloat(),
            selectedDuration != "All",
            debouncedSearchQuery.isNotBlank()
        ).count { it }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discover Amazing Trips",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary            )
            
            AnimatedVisibility(
                visible = activeFilterCount > 0,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Active filters",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AnimatedContent(
                            targetState = activeFilterCount,
                            transitionSpec = {
                                slideInVertically(
                                    initialOffsetY = { if (targetState > initialState) -it else it },
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutVertically(
                                    targetOffsetY = { if (targetState > initialState) it else -it },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            },
                            label = "filter count"
                        ) { count ->
                            Text(
                                text = "$count filter${if (count == 1) "" else "s"}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

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
            },            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (currentLocation.isNotBlank()) {
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
        )        // Active Filters and Results Count (using debounced search for consistency)
        val hasActiveFilters = selectedCategory != "All" || 
            priceRangeStart != minPrice.toFloat() || 
            priceRangeEnd != maxPrice.toFloat() || 
            selectedDuration != "All" ||
            debouncedSearchQuery.isNotBlank()

        AnimatedVisibility(
            visible = hasActiveFilters,
            enter = slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(400, easing = EaseOutCubic)
            ) + fadeIn(animationSpec = tween(400)),
            exit = slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(300, easing = EaseInCubic)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = filteredTrips.size,
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { if (targetState > initialState) it else -it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                        slideOutVertically(
                            targetOffsetY = { if (targetState > initialState) -it else it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    },
                    label = "results count"
                ) { count ->
                    Text(
                        text = "$count trips found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }                TextButton(
                    onClick = {
                        // Clear all filters and preferences
                        searchQuery = ""
                        debouncedSearchQuery = ""
                        selectedCategory = "All"
                        priceRangeStart = minPrice.toFloat()
                        priceRangeEnd = maxPrice.toFloat()
                        selectedDuration = "All"
                        clearAllFilterPreferences()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear filters",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear all")
                }
            }
        }// Filter Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Filter
            FilterChip(
                label = if (selectedCategory == "All") "Category" else selectedCategory,
                isActive = selectedCategory != "All",
                onClick = { isCategoryDropdownExpanded = true }
            )

            // Price Range Filter
            val isPriceActive = priceRangeStart != minPrice.toFloat() || priceRangeEnd != maxPrice.toFloat()
            FilterChip(
                label = if (isPriceActive) "$${String.format("%.0f", priceRangeStart)}-$${String.format("%.0f", priceRangeEnd)}" else "Price Range",
                isActive = isPriceActive,
                onClick = { isPriceDialogVisible = true }
            )

            // Duration Filter
            FilterChip(
                label = if (selectedDuration == "All") "Duration" else selectedDuration,
                isActive = selectedDuration != "All",
                onClick = { isDurationDropdownExpanded = true }
            )
        }// Category Dropdown
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
                },                dismissButton = {
                    TextButton(
                        onClick = {
                            // Reset to original range and clear preferences
                            priceRangeStart = minPrice.toFloat()
                            priceRangeEnd = maxPrice.toFloat()
                            isPriceDialogVisible = false
                            // Update preferences immediately
                            saveFilterPreferences(selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration, searchQuery)
                        }
                    ) {
                        Text("Reset")
                    }
                })
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
        }        // Trip Cards
        AnimatedContent(
            targetState = filteredTrips,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith
                fadeOut(animationSpec = tween(200))
            },
            label = "trip cards"
        ) { trips ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = trips,
                    key = { trip -> trip.id }
                ) { trip ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(300, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(300)),
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(300, easing = EaseOutCubic)
                        )
                    ) {
                        TripCard(
                            trip = trip,
                            onReserveClick = { onTripClick(trip.id) }
                        )
                    }
                }
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
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip scale"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        animationSpec = tween(300),
        label = "container color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
        animationSpec = tween(300),
        label = "content color"
    )
    
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        AnimatedContent(
            targetState = label,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                fadeOut(animationSpec = tween(200))
            },
            label = "chip label"
        ) { currentLabel ->
            Text(
                text = currentLabel,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
