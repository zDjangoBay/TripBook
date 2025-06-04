package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.service.AgencyService
import com.android.tripbook.service.TravelAgency
import com.android.tripbook.service.TravelAgencyService

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
    var isLoading by remember { mutableStateOf(false) }
    var agencies by remember { mutableStateOf<List<TravelAgency>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load agencies when screen loads or destination changes
    LaunchedEffect(destination) {
        isLoading = true
        errorMessage = null
        try {
            val allAgencies = travelAgencyService.getAgenciesForDestination(destination)
            agencies = travelAgencyService.filterAgencies(allAgencies, minRating, maxPrice, serviceType)
            println("Loaded ${agencies.size} agencies for $destination")
        } catch (e: Exception) {
            errorMessage = "Failed to load travel agencies. Please check your connection."
            println("Error loading agencies: ${e.message}")
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Update filter results
    LaunchedEffect(minRating, maxPrice, serviceType) {
        if (agencies.isNotEmpty() && !isLoading) {
            try {
                val allAgencies = travelAgencyService.getAgenciesForDestination(destination)
                agencies = travelAgencyService.filterAgencies(allAgencies, minRating, maxPrice, serviceType)
            } catch (e: Exception) {
                println("Error filtering agencies: ${e.message}")
            }
        }
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
                Column {
                    Text(
                        text = "Travel Agencies in",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = destination,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
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
                    // Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter Services",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )
                        if (agencies.any { it.isFromApi }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Live Data Available",
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = minRating != null,
                                onClick = {
                                    minRating = if (minRating == null) 4.0f else null
                                },
                                label = { Text("Rating 4.0+") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF667EEA),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = maxPrice != null,
                                onClick = {
                                    maxPrice = if (maxPrice == null) 300 else null
                                },
                                label = { Text("Price ≤ $300") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF667EEA),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = serviceType != null,
                                onClick = {
                                    serviceType = if (serviceType == null) "Tour" else null
                                },
                                label = { Text("Tours Only") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF667EEA),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Content
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF667EEA)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Finding travel agencies...",
                                        fontSize = 16.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                        errorMessage != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Button(onClick = {
                                    // Retry loading agencies
                                    isLoading = true
                                    errorMessage = null
                                }) {
                                    Text("Retry")
                                }
                            }
                        }
                        agencies.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No travel agencies found for $destination",
                                    fontSize = 16.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Try adjusting filters or selecting a different destination",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(agencies) { agency ->
                                    EnhancedAgencyCard(
                                        agency = agency,
                                        onServiceClick = { service ->
                                            val itineraryType = when (service.type) {
                                                "Tour" -> ItineraryType.ACTIVITY
                                                "Accommodation" -> ItineraryType.ACCOMMODATION
                                                "Transportation" -> ItineraryType.TRANSPORTATION
                                                else -> ItineraryType.ACTIVITY
                                            }
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
private fun EnhancedAgencyCard(agency: TravelAgency, onServiceClick: (AgencyService) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (agency.isFromApi) Color(0xFFECFDF5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (agency.isFromApi) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = agency.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                    if (agency.address != null) {
                        Text(
                            text = agency.address,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            maxLines = 2,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    // Assuming 'category' might be a new field in TravelAgency for display
                    // if (agency.category != null) {
                    //    Text(
                    //        text = agency.category,
                    //        fontSize = 11.sp,
                    //        color = Color(0xFF667EEA),
                    //        fontWeight = FontWeight.Medium,
                    //        modifier = Modifier.padding(top = 2.dp)
                    //    )
                    //}
                }

                Column(horizontalAlignment = Alignment.End) {
                    // API source indicator
                    if (agency.isFromApi) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", agency.rating),
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Services
            Text(
                text = "Available Services:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            agency.services.forEach { service ->
                EnhancedServiceItem(service = service, onClick = { onServiceClick(service) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EnhancedServiceItem(service: AgencyService, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                service.isFromApi -> Color(0xFFF0FDF4)
                else -> Color(0xFFF1F5F9)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = service.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A202C)
                        )
                        if (service.isFromApi) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.height(16.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = service.description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Assuming 'distance' might be a new field in AgencyService for display
                    // if (service.distance != null) {
                    //    Text(
                    //        text = service.distance,
                    //        fontSize = 12.sp,
                    //        color = Color(0xFF667EEA),
                    //        fontWeight = FontWeight.Medium,
                    //        modifier = Modifier.padding(top = 2.dp)
                    //    )
                    //}
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${service.price}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667EEA)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Service Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format("%.1f", service.rating),
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}