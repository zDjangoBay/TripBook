package com.android.tripbook.ui.uis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.NotificationPreferences
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    preferences: NotificationPreferences,
    onPreferencesChanged: (NotificationPreferences) -> Unit,
    onBackClick: () -> Unit
) {
    var currentPreferences by remember { mutableStateOf(preferences) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Notification Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF667EEA),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF667EEA),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Trip Notifications",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Stay updated on your travel milestones",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
            
            // Main Settings
            SettingsSection(
                title = "General Settings",
                content = {
                    SettingsSwitchItem(
                        title = "Enable Push Notifications",
                        description = "Receive notifications about your trips",
                        checked = currentPreferences.enablePushNotifications,
                        onCheckedChange = { 
                            currentPreferences = currentPreferences.copy(enablePushNotifications = it)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                }
            )
            
            // Trip Reminders
            SettingsSection(
                title = "Trip Reminders",
                content = {
                    SettingsSwitchItem(
                        title = "Trip Start Reminders",
                        description = "Get notified before your trip starts",
                        checked = currentPreferences.enableTripStartReminders,
                        enabled = currentPreferences.enablePushNotifications,
                        onCheckedChange = { 
                            currentPreferences = currentPreferences.copy(enableTripStartReminders = it)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Activity Reminders",
                        description = "Reminders to add activities to your itinerary",
                        checked = currentPreferences.enableActivityReminders,
                        enabled = currentPreferences.enablePushNotifications,
                        onCheckedChange = { 
                            currentPreferences = currentPreferences.copy(enableActivityReminders = it)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Budget Reminders",
                        description = "Track your trip expenses",
                        checked = currentPreferences.enableBudgetReminders,
                        enabled = currentPreferences.enablePushNotifications,
                        onCheckedChange = { 
                            currentPreferences = currentPreferences.copy(enableBudgetReminders = it)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Packing Reminders",
                        description = "Don't forget to pack for your trip",
                        checked = currentPreferences.enablePackingReminders,
                        enabled = currentPreferences.enablePushNotifications,
                        onCheckedChange = { 
                            currentPreferences = currentPreferences.copy(enablePackingReminders = it)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Document Reminders",
                        description = "Check your travel documents",
                        checked = currentPreferences.enableDocumentReminders,
                        enabled = currentPreferences.enablePushNotifications,
                        onCheckedChange = { 
                            currentPreferences = currentPreferences.copy(enableDocumentReminders = it)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                }
            )
            
            // Timing Settings
            SettingsSection(
                title = "Timing Settings",
                content = {
                    QuietHoursSettings(
                        startHour = currentPreferences.quietHoursStart,
                        endHour = currentPreferences.quietHoursEnd,
                        enabled = currentPreferences.enablePushNotifications,
                        onStartHourChanged = { hour ->
                            currentPreferences = currentPreferences.copy(quietHoursStart = hour)
                            onPreferencesChanged(currentPreferences)
                        },
                        onEndHourChanged = { hour ->
                            currentPreferences = currentPreferences.copy(quietHoursEnd = hour)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                    
                    ReminderDaysSettings(
                        reminderDays = currentPreferences.tripStartReminderDays,
                        enabled = currentPreferences.enablePushNotifications && currentPreferences.enableTripStartReminders,
                        onReminderDaysChanged = { days ->
                            currentPreferences = currentPreferences.copy(tripStartReminderDays = days)
                            onPreferencesChanged(currentPreferences)
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) Color(0xFF1A1A1A) else Color(0xFF999999)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = if (enabled) Color(0xFF666666) else Color(0xFF999999)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF667EEA),
                checkedTrackColor = Color(0xFF667EEA).copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun QuietHoursSettings(
    startHour: Int,
    endHour: Int,
    enabled: Boolean,
    onStartHourChanged: (Int) -> Unit,
    onEndHourChanged: (Int) -> Unit
) {
    Column {
        Text(
            text = "Quiet Hours",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) Color(0xFF1A1A1A) else Color(0xFF999999),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "No notifications during these hours",
            fontSize = 12.sp,
            color = if (enabled) Color(0xFF666666) else Color(0xFF999999),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "From: ${String.format("%02d:00", startHour)}",
                fontSize = 14.sp,
                color = if (enabled) Color(0xFF1A1A1A) else Color(0xFF999999)
            )
            Text(
                text = "To: ${String.format("%02d:00", endHour)}",
                fontSize = 14.sp,
                color = if (enabled) Color(0xFF1A1A1A) else Color(0xFF999999)
            )
        }
    }
}

@Composable
fun ReminderDaysSettings(
    reminderDays: List<Int>,
    enabled: Boolean,
    onReminderDaysChanged: (List<Int>) -> Unit
) {
    Column {
        Text(
            text = "Trip Start Reminders",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) Color(0xFF1A1A1A) else Color(0xFF999999),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Get notified ${reminderDays.joinToString(", ")} days before your trip",
            fontSize = 12.sp,
            color = if (enabled) Color(0xFF666666) else Color(0xFF999999)
        )
    }
}
