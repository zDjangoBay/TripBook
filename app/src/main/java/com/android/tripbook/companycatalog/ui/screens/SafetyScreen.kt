@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            "Emergency Contacts",
            "Always share your travel itinerary with someone back home",
            Icons.Default.Warning,
            "High"
        ),
        SafetyTip(
            "Local Laws",
            "Research local customs and laws before traveling",
            Icons.Default.Warning,
            "Medium"
        ),
        SafetyTip(
            "Health Precautions",
            "Get necessary vaccinations and carry basic medications",
            Icons.Default.Warning,
            "High"
        ),
        SafetyTip(
            "Money Safety",
            "Use multiple payment methods and avoid carrying large cash amounts",
            Icons.Default.Warning,
            "Medium"
        ),
        SafetyTip(
            "Communication",
            "Keep your phone charged and have offline maps downloaded",
            Icons.Default.Warning,
            "High"
        ),
        SafetyTip(
            "Transportation",
            "Use reputable transportation services and avoid traveling alone at night",
            Icons.Default.Warning,
            "Medium"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Safety Tips",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(safetyTips) { tip ->
                SafetyTipCard(tip = tip)
            }
        }
    }
}

@Composable
fun SafetyTipCard(tip: SafetyTip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tip.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = when (tip.priority) {
                    "High" -> MaterialTheme.colorScheme.error
                    "Medium" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Badge(
                containerColor = when (tip.priority) {
                    "High" -> MaterialTheme.colorScheme.error
                    "Medium" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }
            ) {
                Text(
                    text = tip.priority,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
