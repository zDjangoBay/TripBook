@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SafetyTip(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val priority: String
)

@Composable
fun SafetyScreen() {
    val safetyTips = listOf(
        SafetyTip(
            title = "Check Weather Conditions",
            description = "Always check the weather forecast before starting your trip.",
            icon = Icons.Default.Warning,
            priority = "High"
        ),
        SafetyTip(
            title = "Keep Emergency Contacts",
            description = "Maintain a list of emergency contacts and share your itinerary.",
            icon = Icons.Default.Warning,
            priority = "High"
        ),
        SafetyTip(
            title = "Pack First Aid Kit",
            description = "Always carry a basic first aid kit for minor injuries.",
            icon = Icons.Default.Warning,
            priority = "Medium"
        ),
        SafetyTip(
            title = "Stay Hydrated",
            description = "Carry enough water and stay hydrated throughout your journey.",
            icon = Icons.Default.Warning,
            priority = "High"
        ),
        SafetyTip(
            title = "Inform Someone",
            description = "Let someone know your travel plans and expected return time.",
            icon = Icons.Default.Warning,
            priority = "High"
        ),
        SafetyTip(
            title = "Carry Identification",
            description = "Always carry proper identification and important documents.",
            icon = Icons.Default.Warning,
            priority = "Medium"
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(safetyTips) { tip ->
            SafetyTipCard(tip = tip)
        }
    }
}

@Composable
fun SafetyTipCard(tip: SafetyTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Icon(
                    imageVector = tip.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Priority: ${tip.priority}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}