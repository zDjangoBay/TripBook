package com.android.tripbook.datamining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.tripbook.datamining.data.model.Airport
import com.android.tripbook.datamining.data.model.Amenity
import com.android.tripbook.datamining.data.model.AmenityType
import com.android.tripbook.datamining.data.model.Terminal
import com.android.tripbook.datamining.data.model.TransportationOption
import com.android.tripbook.datamining.data.model.TransportationType

/**
 * Component for displaying detailed airport information
 */
@Composable
fun AirportDetails(
    airport: Airport?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (airport == null) {
            Text(
                text = "Airport details not available",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Airport image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(airport.imageUrl ?: "https://images.unsplash.com/photo-1583664426440-daef00e4ad6d")
                            .crossfade(true)
                            .build(),
                        contentDescription = airport.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Airport code badge
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = airport.iataCode,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                
                // Airport name and location
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = airport.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${airport.city}, ${airport.country}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quick info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoItem(
                            icon = if (airport.hasWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
                            label = if (airport.hasWifi) "WiFi Available" else "No WiFi",
                            tint = if (airport.hasWifi) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        
                        InfoItem(
                            icon = Icons.Default.AttachMoney,
                            label = if (airport.hasCurrencyExchange) "Currency Exchange" else "No Currency Exchange",
                            tint = if (airport.hasCurrencyExchange) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        
                        InfoItem(
                            icon = Icons.Default.Flight,
                            label = "${airport.majorAirlines.size} Airlines",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Transportation options
                    SectionTitle(title = "Transportation Options")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (airport.transportationOptions.isEmpty()) {
                        Text(
                            text = "Transportation information not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            airport.transportationOptions.forEach { option ->
                                TransportationOptionItem(option = option)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Terminals and amenities
                    SectionTitle(title = "Terminals & Amenities")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (airport.terminals.isEmpty()) {
                        Text(
                            text = "Terminal information not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            airport.terminals.forEach { terminal ->
                                TerminalItem(terminal = terminal)
                            }
                        }
                    }
                    
                    if (airport.amenities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Airport Amenities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            airport.amenities.forEach { amenity ->
                                AmenityItem(amenity = amenity)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Flight information
                    SectionTitle(title = "Flight Information")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Major airlines
                    if (airport.majorAirlines.isNotEmpty()) {
                        Text(
                            text = "Major Airlines",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = airport.majorAirlines.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Popular destinations
                    if (airport.popularDestinations.isNotEmpty()) {
                        Text(
                            text = "Popular Destinations",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = airport.popularDestinations.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Seasonal notes
                    if (airport.seasonalNotes.isNotEmpty()) {
                        Text(
                            text = "Seasonal Notes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = airport.seasonalNotes,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Known issues
                    if (airport.knownIssues.isNotEmpty()) {
                        Text(
                            text = "Known Issues",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = airport.knownIssues,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Immigration information
                    SectionTitle(title = "Immigration Information")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (airport.immigrationInfo.isEmpty()) {
                        Text(
                            text = "Immigration information not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = airport.immigrationInfo,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Additional information
                    SectionTitle(title = "Additional Information")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Coordinates
                    InfoRow(
                        label = "Coordinates",
                        value = "${airport.latitude}, ${airport.longitude}"
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Elevation
                    InfoRow(
                        label = "Elevation",
                        value = "${airport.elevation} ft"
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Distance from city center
                    InfoRow(
                        label = "Distance from City Center",
                        value = "${airport.distanceFromCityCenter} km"
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Timezone
                    InfoRow(
                        label = "Timezone",
                        value = airport.timezone
                    )
                    
                    // Website
                    if (airport.websiteUrl != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        InfoRow(
                            label = "Website",
                            value = airport.websiteUrl
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(tint.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TransportationOptionItem(option: TransportationOption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on transportation type
            val icon = when (option.type) {
                TransportationType.TAXI, TransportationType.RIDESHARE -> Icons.Default.DirectionsCar
                TransportationType.BUS, TransportationType.SHUTTLE -> Icons.Default.DirectionsBus
                TransportationType.TRAIN, TransportationType.METRO -> Icons.Default.Train
                else -> Icons.Default.DirectionsCar
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Cost: ${option.estimatedCost} ${option.currency} • Duration: ${option.duration}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (option.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = option.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TerminalItem(terminal: Terminal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = terminal.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (terminal.airlines.isNotEmpty()) {
                Text(
                    text = "Airlines: ${terminal.airlines.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            if (terminal.gates.isNotEmpty()) {
                Text(
                    text = "Gates: ${terminal.gates}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            if (terminal.notes.isNotEmpty()) {
                Text(
                    text = terminal.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            if (terminal.amenities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Terminal Amenities",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    terminal.amenities.forEach { amenity ->
                        AmenityItem(amenity = amenity, compact = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun AmenityItem(amenity: Amenity, compact: Boolean = false) {
    val icon = when (amenity.type) {
        AmenityType.RESTAURANT, AmenityType.CAFE, AmenityType.BAR -> Icons.Default.Restaurant
        AmenityType.SHOP, AmenityType.DUTY_FREE -> Icons.Default.ShoppingCart
        AmenityType.CURRENCY_EXCHANGE -> Icons.Default.AttachMoney
        AmenityType.INFORMATION_DESK -> Icons.Default.Info
        else -> Icons.Default.Info
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        if (compact) {
            Text(
                text = amenity.name,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        } else {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = amenity.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (amenity.location.isNotEmpty()) {
                    Text(
                        text = amenity.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (amenity.description.isNotEmpty()) {
                    Text(
                        text = amenity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (amenity.rating != null) {
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "${amenity.rating}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(160.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
