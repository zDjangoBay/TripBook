package com.android.tripbook.ui.screens.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.models.TransportOption
import com.android.tripbook.data.models.TransportType
import com.android.tripbook.data.providers.DummyTripDataProvider

/**
 * First step: Transport type selection
 */
@Composable
fun TransportSelectionStep(
    onTransportSelected: (TransportOption) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Choose Your Transport",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Select your preferred mode of transportation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Transport options
        val transportOptions = listOf(
            TransportOptionData(
                type = TransportType.PLANE,
                icon = Icons.Default.Flight,
                title = "Airplane",
                description = "Fast and comfortable",
                price = 120.0
            ),
            TransportOptionData(
                type = TransportType.CAR,
                icon = Icons.Default.DirectionsCar,
                title = "Car",
                description = "Flexible and scenic",
                price = 60.0
            ),
            TransportOptionData(
                type = TransportType.SHIP,
                icon = Icons.Default.DirectionsBoat,
                title = "Ship",
                description = "Relaxing journey",
                price = 80.0
            )
        )
        
        transportOptions.forEach { option ->
            TransportOptionCard(
                option = option,
                onClick = {
                    // Get the first transport option of this type
                    val transportOption = DummyTripDataProvider.getTransportOptionsByType(option.type).firstOrNull()
                    transportOption?.let { onTransportSelected(it) }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportOptionCard(
    option: TransportOptionData,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${String.format("%.0f", option.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "per person",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class TransportOptionData(
    val type: TransportType,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val price: Double
)
