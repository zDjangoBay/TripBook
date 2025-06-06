package com.android.tripbook.ui.notifications.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.notifications.viewmodels.NotificationFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit,
    unreadCount: Int,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            items = listOf(
                FilterChipData(NotificationFilter.ALL, "Toutes", null),
                FilterChipData(NotificationFilter.UNREAD, "Non lues", unreadCount),
                FilterChipData(NotificationFilter.BOOKINGS, "Réservations", null),
                FilterChipData(NotificationFilter.PAYMENTS, "Paiements", null),
                FilterChipData(NotificationFilter.REMINDERS, "Rappels", null)
            )
        ) { chipData ->
            FilterChip(
                onClick = { onFilterSelected(chipData.filter) },
                label = {
                    Row {
                        Text(chipData.label)
                        if (chipData.count != null && chipData.count > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge {
                                Text(
                                    text = chipData.count.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                },
                selected = selectedFilter == chipData.filter,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

private data class FilterChipData(
    val filter: NotificationFilter,
    val label: String,
    val count: Int?
)