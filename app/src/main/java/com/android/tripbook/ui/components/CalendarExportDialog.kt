package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.android.tripbook.model.Trip
import com.android.tripbook.ui.theme.TripBookColors
import com.android.tripbook.utils.CalendarUtils

enum class CalendarExportOption {
    TRIP_OVERVIEW,
    INDIVIDUAL_ACTIVITIES,
    BOTH
}

@Composable
fun CalendarExportDialog(
    trip: Trip,
    onDismiss: () -> Unit,
    onExportComplete: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableStateOf(CalendarExportOption.TRIP_OVERVIEW) }
    var isExporting by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Calendar",
                    tint = TripBookColors.Primary,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Save to Calendar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Choose what you'd like to add to your calendar",
                    fontSize = 14.sp,
                    color = TripBookColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Export options
                CalendarExportOptionItem(
                    icon = Icons.Default.Event,
                    title = "Trip Overview",
                    description = "Add the entire trip as a single calendar event",
                    isSelected = selectedOption == CalendarExportOption.TRIP_OVERVIEW,
                    onClick = { selectedOption = CalendarExportOption.TRIP_OVERVIEW }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                CalendarExportOptionItem(
                    icon = Icons.Default.Schedule,
                    title = "Individual Activities",
                    description = "Add each activity as separate calendar events (${trip.itinerary.size} events)",
                    isSelected = selectedOption == CalendarExportOption.INDIVIDUAL_ACTIVITIES,
                    onClick = { selectedOption = CalendarExportOption.INDIVIDUAL_ACTIVITIES },
                    enabled = trip.itinerary.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                CalendarExportOptionItem(
                    icon = Icons.Default.EventNote,
                    title = "Both",
                    description = "Add trip overview and all individual activities",
                    isSelected = selectedOption == CalendarExportOption.BOTH,
                    onClick = { selectedOption = CalendarExportOption.BOTH },
                    enabled = trip.itinerary.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isExporting,
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Button(
                        onClick = {
                            if (!CalendarUtils.isCalendarAvailable(context)) {
                                onExportComplete(false)
                                return@Button
                            }
                            
                            isExporting = true
                            
                            val success = when (selectedOption) {
                                CalendarExportOption.TRIP_OVERVIEW -> {
                                    val intent = CalendarUtils.createTripCalendarIntent(trip)
                                    CalendarUtils.launchCalendarIntent(context, intent)
                                }
                                CalendarExportOption.INDIVIDUAL_ACTIVITIES -> {
                                    val intents = CalendarUtils.createAllItineraryCalendarIntents(trip)
                                    var allSuccessful = true
                                    intents.forEach { intent ->
                                        if (!CalendarUtils.launchCalendarIntent(context, intent)) {
                                            allSuccessful = false
                                        }
                                    }
                                    allSuccessful
                                }
                                CalendarExportOption.BOTH -> {
                                    val tripIntent = CalendarUtils.createTripCalendarIntent(trip)
                                    val tripSuccess = CalendarUtils.launchCalendarIntent(context, tripIntent)
                                    
                                    val itineraryIntents = CalendarUtils.createAllItineraryCalendarIntents(trip)
                                    var itinerarySuccess = true
                                    itineraryIntents.forEach { intent ->
                                        if (!CalendarUtils.launchCalendarIntent(context, intent)) {
                                            itinerarySuccess = false
                                        }
                                    }
                                    
                                    tripSuccess && itinerarySuccess
                                }
                            }
                            
                            isExporting = false
                            onExportComplete(success)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TripBookColors.Primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Export",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarExportOptionItem(
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                enabled = enabled
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) TripBookColors.Primary.copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) {
            ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(TripBookColors.Primary)
            )
        } else {
            ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE0E0E0))
            )
        },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (enabled) {
                    if (isSelected) TripBookColors.Primary else TripBookColors.TextSecondary
                } else {
                    Color.Gray
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) {
                        if (isSelected) TripBookColors.Primary else TripBookColors.TextPrimary
                    } else {
                        Color.Gray
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = if (enabled) TripBookColors.TextSecondary else Color.Gray
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = TripBookColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
