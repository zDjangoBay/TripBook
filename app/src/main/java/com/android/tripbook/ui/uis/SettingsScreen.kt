package com.android.tripbook.ui.uis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var autoSyncEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Notifications Setting
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = "Notifications",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Receive trip reminders and updates",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Dark Mode Setting
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = "Dark Mode",
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = "Dark Mode",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Switch to dark theme",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = darkModeEnabled,
                                onCheckedChange = { darkModeEnabled = it }
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Auto Sync Setting
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Auto Sync",
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = "Auto Sync",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Automatically sync trip data",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = autoSyncEnabled,
                                onCheckedChange = { autoSyncEnabled = it }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Account",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Account Settings Items
                        SettingsItem(
                            icon = Icons.Default.AccountCircle,
                            title = "Account Information",
                            subtitle = "Manage your account details"
                        )

                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Privacy & Security",
                            subtitle = "Control your privacy settings"
                        )

                        SettingsItem(
                            icon = Icons.Default.Help,
                            title = "Help & Support",
                            subtitle = "Get help and contact support"
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "App Version",
                            subtitle = "1.0.0"
                        )

                        SettingsItem(
                            icon = Icons.Default.Description,
                            title = "Terms of Service",
                            subtitle = "Read our terms and conditions"
                        )

                        SettingsItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            subtitle = "Learn about data handling"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}