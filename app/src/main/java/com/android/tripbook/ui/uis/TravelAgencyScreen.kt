package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.android.tripbook.service.TravelAgencyService
import com.android.tripbook.service.TravelAgencyServiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAgencyScreen(
    destination: String,
    onBack: () -> Unit,
    onServiceSelected: (TravelAgencyServiceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val travelAgencyService = remember { TravelAgencyService() }
    val coroutineScope = rememberCoroutineScope()

    // State management
    var services by remember { mutableStateOf<List<TravelAgencyServiceItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    // Filter states
    var selectedServiceType by remember { mutableStateOf<String?>(null) }
    var minRating by remember { mutableFloatStateOf(0f) }
    var maxPrice by remember { mutableStateOf(10000.0) }
    var filteredServices by remember { mutableStateOf<List<TravelAgencyServiceItem>>(emptyList()) }

    // Function to load services
    fun loadServices() {
        coroutineScope.launch {
            try {
                isLoading = true
                errorMessage = null
                val loadedServices = withContext(Dispatchers.IO) {
                    travelAgencyService.getServicesForDestination(destination)
                }
                services = loadedServices
                filteredServices = loadedServices
            } catch (e: Exception) {
                errorMessage = "Failed to load services: ${e.message}"
                services = emptyList()
                filteredServices = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    // Load services when destination changes
    LaunchedEffect(destination) {
        if (destination.isNotBlank()) {
            loadServices()
        }
    }

    // Apply filters when filter criteria change
    LaunchedEffect(services, selectedServiceType, minRating, maxPrice) {
        filteredServices = if (services.isNotEmpty()) {
            services.filter { service ->
                val matchesType = selectedServiceType == null || service.type == selectedServiceType
                val matchesRating = service.rating >= minRating
                val matchesPrice = service.price <= maxPrice
                matchesType && matchesRating && matchesPrice
            }
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Travel Services - $destination",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filters"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter section
            if (showFilters) {
                FilterSection(
                    availableServiceTypes = services.map { it.type }.distinct(),
                    selectedServiceType = selectedServiceType,
                    onServiceTypeSelected = { selectedServiceType = it },
                    minRating = minRating,
                    onMinRatingChanged = { minRating = it },
                    maxPrice = maxPrice,
                    onMaxPriceChanged = { maxPrice = it },
                    onClearFilters = {
                        selectedServiceType = null
                        minRating = 0f
                        maxPrice = 10000.0
                    }
                )
            }

            // Content section
            when {
                isLoading -> {
                    LoadingContent()
                }

                errorMessage != null -> {
                    ErrorContent(
                        errorMessage = errorMessage!!,
                        onRetry = ::loadServices
                    )
                }

                filteredServices.isEmpty() && services.isNotEmpty() -> {
                    NoFilterResultsContent()
                }

                services.isEmpty() && !isLoading -> {
                    NoServicesContent(destination = destination)
                }

                else -> {
                    ServicesContent(
                        filteredServices = filteredServices,
                        onServiceSelected = onServiceSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading services...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun NoFilterResultsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No services match your filters",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your search criteria",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NoServicesContent(destination: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No services available",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No travel services found for $destination",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ServicesContent(
    filteredServices: List<TravelAgencyServiceItem>,
    onServiceSelected: (TravelAgencyServiceItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Found ${filteredServices.size} services",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(filteredServices) { service ->
            ServiceCard(
                service = service,
                onServiceClick = { onServiceSelected(service) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    availableServiceTypes: List<String>,
    selectedServiceType: String?,
    onServiceTypeSelected: (String?) -> Unit,
    minRating: Float,
    onMinRatingChanged: (Float) -> Unit,
    maxPrice: Double,
    onMaxPriceChanged: (Double) -> Unit,
    onClearFilters: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearFilters) {
                    Text("Clear All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Service Type Filter
            Text(
                text = "Service Type",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedServiceType == null,
                        onClick = { onServiceTypeSelected(null) },
                        label = { Text("All") }
                    )
                }

                items(availableServiceTypes) { type ->
                    FilterChip(
                        selected = selectedServiceType == type,
                        onClick = {
                            onServiceTypeSelected(if (selectedServiceType == type) null else type)
                        },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Filter
            Text(
                text = "Minimum Rating: ${minRating.toInt()}/5",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = minRating,
                onValueChange = onMinRatingChanged,
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price Filter
            Text(
                text = "Maximum Price: $${maxPrice.toInt()}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = maxPrice.toFloat(),
                onValueChange = { onMaxPriceChanged(it.toDouble()) },
                valueRange = 0f..2000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceCard(
    service: TravelAgencyServiceItem,
    onServiceClick: () -> Unit
) {
    ElevatedCard(
        onClick = onServiceClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (service.agencyName.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = service.agencyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", service.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", service.rating),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (service.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { /* Handle service type click if needed */ },
                    label = { Text(service.type) }
                )

                if (service.location.isNotEmpty()) {
                    Text(
                        text = service.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}