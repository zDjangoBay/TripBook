package com.android.tripbook.ui.notifications.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.ui.notifications.components.FilterChips
import com.android.tripbook.ui.notifications.components.NotificationCard
import com.android.tripbook.ui.notifications.viewmodels.NotificationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel(),
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredNotifications = viewModel.getFilteredNotifications()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header avec titre et actions
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Notifications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.unreadCount > 0) {
                        Text(
                            text = "${uiState.unreadCount} non lue${if (uiState.unreadCount > 1) "s" else ""}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            actions = {
                // Bouton tout marquer comme lu
                if (uiState.unreadCount > 0) {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Tout marquer comme lu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Bouton paramètres
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Paramètres"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Filtres
        FilterChips(
            selectedFilter = uiState.filter,
            onFilterSelected = { viewModel.setFilter(it) },
            unreadCount = uiState.unreadCount,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Liste des notifications
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredNotifications.isEmpty() -> {
                EmptyNotificationsView(
                    filter = uiState.filter,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredNotifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onNotificationClick = { notificationId ->
                                viewModel.markAsRead(notificationId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsView(
    filter: com.android.tripbook.ui.notifications.viewmodels.NotificationFilter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (filter) {
                com.android.tripbook.ui.notifications.viewmodels.NotificationFilter.ALL -> "Aucune notification"
                com.android.tripbook.ui.notifications.viewmodels.NotificationFilter.UNREAD -> "Aucune notification non lue"
                com.android.tripbook.ui.notifications.viewmodels.NotificationFilter.BOOKINGS -> "Aucune notification de réservation"
                com.android.tripbook.ui.notifications.viewmodels.NotificationFilter.PAYMENTS -> "Aucune notification de paiement"
                com.android.tripbook.ui.notifications.viewmodels.NotificationFilter.REMINDERS -> "Aucun rappel"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Text(
            text = "Vous êtes à jour ! ",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}