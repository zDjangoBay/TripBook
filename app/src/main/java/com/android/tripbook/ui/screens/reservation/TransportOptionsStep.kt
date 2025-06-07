package com.android.tripbook.ui.screens.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.models.TransportOption
import com.android.tripbook.data.models.TransportType
import com.android.tripbook.data.providers.DummyTripDataProvider
import java.time.format.DateTimeFormatter

/**
 * Second step: Specific transport option selection
 */
@Composable
fun TransportOptionsStep(
    selectedTransportType: TransportType?,
    onOptionSelected: (TransportOption) -> Unit,
    onBack: () -> Unit
) {
    val transportOptions = remember(selectedTransportType) {
        selectedTransportType?.let { 
            DummyTripDataProvider.getTransportOptionsByType(it)
        } ?: emptyList()
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Select ${selectedTransportType?.name?.lowercase()?.replaceFirstChar { it.uppercase() }} Option",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Choose your preferred ${selectedTransportType?.name?.lowercase()} service",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transportOptions) { option ->
                TransportOptionDetailCard(
                    option = option,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Back button
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Transport Selection")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportOptionDetailCard(
    option: TransportOption,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with name and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "$${String.format("%.0f", option.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Time details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Departure",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = option.departureTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Arrival",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = option.arrivalTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
