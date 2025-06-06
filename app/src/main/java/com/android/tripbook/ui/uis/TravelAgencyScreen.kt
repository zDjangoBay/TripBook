package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.TravelAgency
import com.android.tripbook.service.TravelAgencyService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAgencyScreen(
    destination: String,
    travelAgencyService: TravelAgencyService,
    onBackClick: () -> Unit,
    onServiceSelected: (AgencyService, ItineraryType) -> Unit
) {
    var minRating by remember { mutableStateOf<Float?>(null) }
    var maxPrice by remember { mutableStateOf<Int?>(null) }
    var serviceType by remember { mutableStateOf<String?>(null) }
    var agencies by remember { mutableStateOf<List<TravelAgency>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Validate destination
    val isValidDestination = remember(destination) {
        travelAgencyService.isValidDestination(destination)
    }

    // Load agencies function
    val loadAgencies = {
        if (isValidDestination) {
            scope.launch {
                try {
                    isLoading = true
                    hasError = false
                    val loadedAgencies = travelAgencyService.getFilteredAgencies(
                        destination = destination,
                        minRating = minRating,
                        maxPrice = maxPrice,
                        serviceType = serviceType
                    )
                    agencies = loadedAgencies
                } catch (e: Exception) {
                    hasError = true
                    agencies = emptyList()
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
            hasError = true
            agencies = emptyList()
        }
    }

    // Load agencies when screen starts or filters change
    LaunchedEffect(destination, minRating, maxPrice, serviceType) {
        loadAgencies()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Travel Agencies",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    if (isValidDestination) {
                        Text(
                            text = "in $destination",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
                if (!isLoading && !hasError && agencies.isNotEmpty()) {
                    IconButton(
                        onClick = { loadAgencies() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            }

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    when {
                        !isValidDestination -> {
                            // Invalid destination error
                            ErrorState(
                                title = "Invalid Destination",
                                message = "Please enter a valid destination with at least 2 characters.",
                                onRetryClick = null
                            )
                        }
                        isLoading -> {
                            // Loading state
                            LoadingState()
                        }
                        hasError -> {
                            // Error state
                            ErrorState(
                                title = "Failed to Load Agencies",
                                message = "Please check your connection and try again.",
                                onRetryClick = { loadAgencies() }
                            )
                        }
                        agencies.isEmpty() -> {
                            // Empty state with filters
                            FilterSection(
                                minRating = minRating,
                                maxPrice = maxPrice,
                                serviceType = serviceType,
                                availableServiceTypes = travelAgencyService.getAvailableServiceTypes(),
                                onMinRatingChange = { minRating = it },
                                onMaxPriceChange = { maxPrice = it },
                                onServiceTypeChange = { serviceType = it }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            EmptyState(destination = destination)
                        }
                        else -> {
                            // Success state with agencies
                            FilterSection(
                                minRating = minRating,
                                maxPrice = maxPrice,
                                serviceType = serviceType,
                                availableServiceTypes = travelAgencyService.getAvailableServiceTypes(),
                                onMinRatingChange = { minRating = it },
                                onMaxPriceChange = { maxPrice = it },
                                onServiceTypeChange = { serviceType = it }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Results summary
                            Text(
                                text = "Found ${agencies.size} ${if (agencies.size == 1) "agency" else "agencies"}",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(agencies) { agency ->
                                    AgencyCard(
                                        agency = agency,
                                        onServiceClick = { service ->
                                            val itineraryType = mapServiceTypeToItineraryType(service.type)
                                            onServiceSelected(service, itineraryType)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    minRating: Float?,
    maxPrice: Int?,
    serviceType: String?,
    availableServiceTypes: List<String>,
    onMinRatingChange: (Float?) -> Unit,
    onMaxPriceChange: (Int?) -> Unit,
    onServiceTypeChange: (String?) -> Unit
) {
    Text(
        text = "Filter Services",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151)
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Filter chips
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = minRating != null,
                    onClick = {
                        onMinRatingChange(if (minRating == null) 4.0f else null)
                    },
                    label = { Text("Rating 4.0+") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF667EEA),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = maxPrice != null,
                    onClick = {
                        onMaxPriceChange(if (maxPrice == null) 300 else null)
                    },
                    label = { Text("Price ≤ $300") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF667EEA),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableServiceTypes.forEach { type ->
                    FilterChip(
                        selected = serviceType == type,
                        onClick = {
                            onServiceTypeChange(if (serviceType == type) null else type)
                        },
                        label = { Text("${type}s Only") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF667EEA),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF667EEA),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading travel agencies...",
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(
    title: String,
    message: String,
    onRetryClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
        if (onRetryClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667EEA)
                )
            ) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun EmptyState(destination: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No travel agencies found",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "for \"$destination\"",
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting filters or selecting a different destination",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AgencyCard(agency: TravelAgency, onServiceClick: (AgencyService) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = agency.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${agency.rating}/5.0",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${agency.services.size} ${if (agency.services.size == 1) "service" else "services"})",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            agency.services.forEach { service ->
                ServiceItem(service = service, onClick = { onServiceClick(service) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ServiceItem(service: AgencyService, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A202C)
                    )
                    Text(
                        text = service.description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = service.location,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Surface(
                    modifier = Modifier.padding(start = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF667EEA).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = service.type,
                        fontSize = 12.sp,
                        color = Color(0xFF667EEA),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${service.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667EEA)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Service Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.rating}/5.0",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// Helper function to map service types to itinerary types
private fun mapServiceTypeToItineraryType(serviceType: String): ItineraryType {
    return when (serviceType.lowercase()) {
        "tour" -> ItineraryType.ACTIVITY
        "accommodation" -> ItineraryType.ACCOMMODATION
        "transportation" -> ItineraryType.TRANSPORTATION
        else -> ItineraryType.ACTIVITY
    }
}