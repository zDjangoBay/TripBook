package com.android.tripbook.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.tripbook.model.ReservationFilter
import com.android.tripbook.model.ReservationStatus
import com.android.tripbook.ui.animation.AnimationUtils
import com.android.tripbook.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Dialog for filtering reservations with a modern UI design
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun FilterDialog(
    currentFilter: ReservationFilter,
    onFilterApplied: (ReservationFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(currentFilter.searchQuery ?: "") }
    var selectedStatuses by remember { mutableStateOf(currentFilter.status ?: emptySet()) }
    var priceMin by remember { mutableStateOf(currentFilter.priceMin?.toString() ?: "") }
    var priceMax by remember { mutableStateOf(currentFilter.priceMax?.toString() ?: "") }
    var destination by remember { mutableStateOf(currentFilter.destination ?: "") }

    // Calculate if any filters are active
    val hasActiveFilters = searchQuery.isNotBlank() ||
                          selectedStatuses.isNotEmpty() ||
                          priceMin.isNotBlank() ||
                          priceMax.isNotBlank() ||
                          destination.isNotBlank()

    // Get status icon based on reservation status
    fun getStatusIcon(status: ReservationStatus): ImageVector {
        return when (status) {
            ReservationStatus.CONFIRMED -> Icons.Rounded.CheckCircle
            ReservationStatus.PENDING -> Icons.Rounded.HourglassTop
            ReservationStatus.CANCELLED -> Icons.Rounded.Cancel
            ReservationStatus.COMPLETED -> Icons.Rounded.Done
        }
    }

    // Get status color based on reservation status
    fun getStatusColor(status: ReservationStatus): Color {
        return when (status) {
            ReservationStatus.CONFIRMED -> StatusConfirmed
            ReservationStatus.PENDING -> StatusPending
            ReservationStatus.CANCELLED -> StatusCancelled
            ReservationStatus.COMPLETED -> StatusCompleted
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                // Modern gradient header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    TripBookPrimary,
                                    TripBookSecondary
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FilterList,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Filter Trips",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Active filters indicator
                AnimatedVisibility(
                    visible = hasActiveFilters,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = TripBookPrimary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Active filters applied",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TripBookPrimary
                        )
                    }
                }

                // Filter sections in cards
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search section
                    FilterSectionCard(
                        title = "Search & Destination",
                        icon = Icons.Rounded.Search
                    ) {
                        // Search
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Search keywords") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = if (searchQuery.isNotBlank()) TripBookPrimary else TextSecondary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Destination
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Destination") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = if (destination.isNotBlank()) TripBookPrimary else TextSecondary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Price range section
                    FilterSectionCard(
                        title = "Price Range",
                        icon = Icons.Rounded.AttachMoney
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = priceMin,
                                onValueChange = { priceMin = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min Price") },
                                leadingIcon = {
                                    Text(
                                        text = "$",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (priceMin.isNotBlank()) TripBookPrimary else TextSecondary
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = priceMax,
                                onValueChange = { priceMax = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max Price") },
                                leadingIcon = {
                                    Text(
                                        text = "$",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (priceMax.isNotBlank()) TripBookPrimary else TextSecondary
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Status filter section
                    FilterSectionCard(
                        title = "Reservation Status",
                        icon = Icons.Rounded.EventNote
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReservationStatus.values().forEach { status ->
                                val isSelected = selectedStatuses.contains(status)
                                val statusColor = getStatusColor(status)
                                val statusIcon = getStatusIcon(status)

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedStatuses = if (isSelected) {
                                            selectedStatuses - status
                                        } else {
                                            selectedStatuses + status
                                        }
                                    },
                                    label = {
                                        Text(status.name.lowercase().replaceFirstChar { it.uppercase() })
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = statusIcon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else statusColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = statusColor,
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = statusColor.copy(alpha = 0.5f),
                                        selectedBorderColor = statusColor,
                                        borderWidth = 1.dp,
                                        selectedBorderWidth = 0.dp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Clear button
                    OutlinedButton(
                        onClick = {
                            onFilterApplied(ReservationFilter.empty())
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, TripBookPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Clear All")
                    }

                    // Apply button
                    Button(
                        onClick = {
                            val filter = ReservationFilter(
                                status = if (selectedStatuses.isEmpty()) null else selectedStatuses,
                                destination = destination.ifBlank { null },
                                priceMin = priceMin.toDoubleOrNull(),
                                priceMax = priceMax.toDoubleOrNull(),
                                searchQuery = searchQuery.ifBlank { null }
                            )
                            onFilterApplied(filter)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TripBookPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Apply")
                    }
                }
            }
        }
    }
}

/**
 * Card component for filter sections
 */
@Composable
private fun FilterSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TripBookPrimary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = TextSecondary
                    )
                }
            }

            // Content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}
