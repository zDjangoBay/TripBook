package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.NotificationPreferences
import com.android.tripbook.viewmodel.NotificationViewModel

/**
 * Notification settings screen for user preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: NotificationViewModel = viewModel { 
        NotificationViewModel(application = context.applicationContext as android.app.Application) 
    }
    val uiState by viewModel.uiState.collectAsState()
    
    var preferences by remember { mutableStateOf(uiState.preferences) }
    
    // Update preferences when uiState changes
    LaunchedEffect(uiState.preferences) {
        preferences = uiState.preferences
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF1565C0)
                    )
                )
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Notification Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Notification Types Section
            SettingsSection(title = "Notification Types") {
                SettingsToggleItem(
                    title = "Trip Starting Notifications",
                    description = "Get notified when your trips are about to start",
                    checked = preferences.tripStartNotifications,
                    onCheckedChange = { 
                        preferences = preferences.copy(tripStartNotifications = it)
                        viewModel.updatePreferences(preferences)
                    },
                    icon = Icons.Default.FlightTakeoff
                )
                
                SettingsToggleItem(
                    title = "Trip Ending Notifications",
                    description = "Get notified when your trips are ending",
                    checked = preferences.tripEndNotifications,
                    onCheckedChange = { 
                        preferences = preferences.copy(tripEndNotifications = it)
                        viewModel.updatePreferences(preferences)
                    },
                    icon = Icons.Default.FlightLand
                )
                
                SettingsToggleItem(
                    title = "Itinerary Reminders",
                    description = "Get reminded about upcoming activities",
                    checked = preferences.itineraryReminders,
                    onCheckedChange = { 
                        preferences = preferences.copy(itineraryReminders = it)
                        viewModel.updatePreferences(preferences)
                    },
                    icon = Icons.Default.Schedule
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Timing Settings Section
            SettingsSection(title = "Timing Settings") {
                SettingsSliderItem(
                    title = "Trip Start Advance Notice",
                    description = "How many days before trip start to notify",
                    value = preferences.tripStartAdvanceDays.toFloat(),
                    valueRange = 1f..7f,
                    steps = 5,
                    onValueChange = { 
                        preferences = preferences.copy(tripStartAdvanceDays = it.toInt())
                        viewModel.updatePreferences(preferences)
                    },
                    valueFormatter = { "${it.toInt()} days" }
                )
                
                SettingsSliderItem(
                    title = "Activity Advance Notice",
                    description = "How many hours before activity to notify",
                    value = preferences.itineraryAdvanceHours.toFloat(),
                    valueRange = 1f..12f,
                    steps = 10,
                    onValueChange = { 
                        preferences = preferences.copy(itineraryAdvanceHours = it.toInt())
                        viewModel.updatePreferences(preferences)
                    },
                    valueFormatter = { "${it.toInt()} hours" }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quiet Hours Section
            SettingsSection(title = "Quiet Hours") {
                SettingsToggleItem(
                    title = "Enable Quiet Hours",
                    description = "Disable notifications during specified hours",
                    checked = preferences.quietHoursEnabled,
                    onCheckedChange = { 
                        preferences = preferences.copy(quietHoursEnabled = it)
                        viewModel.updatePreferences(preferences)
                    },
                    icon = Icons.Default.DoNotDisturb
                )
                
                if (preferences.quietHoursEnabled) {
                    SettingsTimeItem(
                        title = "Quiet Hours Start",
                        time = preferences.quietHoursStart,
                        onTimeChange = { 
                            preferences = preferences.copy(quietHoursStart = it)
                            viewModel.updatePreferences(preferences)
                        }
                    )
                    
                    SettingsTimeItem(
                        title = "Quiet Hours End",
                        time = preferences.quietHoursEnd,
                        onTimeChange = { 
                            preferences = preferences.copy(quietHoursEnd = it)
                            viewModel.updatePreferences(preferences)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sound & Vibration Section
            SettingsSection(title = "Sound & Vibration") {
                SettingsToggleItem(
                    title = "Sound",
                    description = "Play sound for notifications",
                    checked = preferences.soundEnabled,
                    onCheckedChange = { 
                        preferences = preferences.copy(soundEnabled = it)
                        viewModel.updatePreferences(preferences)
                    },
                    icon = Icons.Default.VolumeUp
                )
                
                SettingsToggleItem(
                    title = "Vibration",
                    description = "Vibrate for notifications",
                    checked = preferences.vibrationEnabled,
                    onCheckedChange = { 
                        preferences = preferences.copy(vibrationEnabled = it)
                        viewModel.updatePreferences(preferences)
                    },
                    icon = Icons.Default.Vibration
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Test Section
            SettingsSection(title = "Testing") {
                OutlinedButton(
                    onClick = { viewModel.sendTestNotification() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Test Notification")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = { viewModel.runNotificationTests() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run Full Test Suite")
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsSliderItem(
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = valueFormatter(value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun SettingsTimeItem(
    title: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        OutlinedButton(
            onClick = { 
                // In a real app, this would open a time picker
                // For now, just cycle through some preset times
                val times = listOf("22:00", "23:00", "00:00", "06:00", "07:00", "08:00")
                val currentIndex = times.indexOf(time)
                val nextIndex = (currentIndex + 1) % times.size
                onTimeChange(times[nextIndex])
            }
        ) {
            Text(time)
        }
    }
}
