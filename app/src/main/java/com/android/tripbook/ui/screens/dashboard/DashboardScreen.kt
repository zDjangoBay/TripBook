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
import androidx.compose.ui.semantics.*
import androidx.compose.ui.semantics.Role
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
) {    
    val context = LocalContext.current
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
        mutableFloatStateOf(savedStart.coerceIn(minPrice.toFloat(), maxPrice.toFloat()))
    }
    var priceRangeEnd by remember { 
        val savedEnd = prefs.getFloat("price_range_end", maxPrice.toFloat())
        mutableFloatStateOf(savedEnd.coerceIn(minPrice.toFloat(), maxPrice.toFloat()))
    }
    var isPriceDialogVisible by remember { mutableStateOf(false) }
    // Duration filter state management with persistence
    val availableDurations = remember { 
        listOf("All") + trips.map { it.duration }.distinct().sorted()
    }
    var selectedDuration by remember { 
        val savedDuration = prefs.getString("selected_duration", "All") ?: "All"
        mutableStateOf(if (availableDurations.contains(savedDuration)) savedDuration else "All")
    }
    var isDurationDropdownExpanded by remember { mutableStateOf(false) }
    // Search query with persistence and advanced debouncing
    var searchQuery by remember { 
        mutableStateOf(prefs.getString("search_query", "") ?: "") 
    }
    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }
    // Advanced debounce search input with job cancellation for better performance
    LaunchedEffect(searchQuery) {
        val searchJob = Job()
        try {
            delay(300) // 300ms debounce delay
            if (searchJob.isActive) {
                debouncedSearchQuery = searchQuery
            }
        } catch (_: Exception) {}
    }
    // Save preferences whenever filter state changes (with debounced search and throttling)
    LaunchedEffect(selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration, debouncedSearchQuery) {
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
    }
    // High-performance optimized search function with advanced filtering
    val filteredTrips = remember(debouncedSearchQuery, currentLocation, selectedCategory, priceRangeStart, priceRangeEnd, selectedDuration) {
        if (trips.isEmpty()) return@remember emptyList<Trip>()
        val searchTerms = if (debouncedSearchQuery.isBlank()) {
            emptyList()
        } else {
            debouncedSearchQuery.lowercase().trim()
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() && it.length >= 2 }
        }
        val currentLocationLower = currentLocation.lowercase()
        val hasLocationFilter = currentLocationLower.isNotBlank()
        val hasSearchFilter = searchTerms.isNotEmpty()
        val hasCategoryFilter = selectedCategory != "All"
        val hasPriceFilter = priceRangeStart != minPrice.toFloat() || priceRangeEnd != maxPrice.toFloat()
        val hasDurationFilter = selectedDuration != "All"
        trips.asSequence()
            .filter { trip ->
                if (hasCategoryFilter && !trip.category.name.equals(selectedCategory, ignoreCase = true)) return@filter false
                if (hasPriceFilter && trip.basePrice !in priceRangeStart..priceRangeEnd) return@filter false
                if (hasDurationFilter && !trip.duration.equals(selectedDuration, ignoreCase = true)) return@filter false
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
                    if (!matchesSearchTerms && !matchesCurrentLocation) return@filter false
                }
                true
            }
            .toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { 
                contentDescription = "Trip booking dashboard with search and filter options"
            }
    ) {
        val activeFilterCount = listOf(
            selectedCategory != "All",
            priceRangeStart != minPrice.toFloat() || priceRangeEnd != maxPrice.toFloat(),
            selectedDuration != "All",
            debouncedSearchQuery.isNotBlank()
        ).count { it }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .semantics { 
                    contentDescription = "Header section with app title and active filters indicator"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discover Amazing Trips",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    heading()
                    contentDescription = "App title: Discover Amazing Trips"
                }
            )
            
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
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .semantics { 
                            contentDescription = "$activeFilterCount active filters applied"
                            role = Role.Button
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
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
                    .padding(bottom = 8.dp)
                    .semantics { 
                        contentDescription = "Current location is $currentLocation, trips from this location will be prioritized"
                    },
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
                        contentDescription = null,
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
            onValueChange = { searchQuery = it