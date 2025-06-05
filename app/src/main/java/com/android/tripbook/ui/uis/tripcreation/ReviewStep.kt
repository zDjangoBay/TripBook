package com.android.tripbook.ui.uis.tripcreation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.ui.components.StepHeader
import java.time.format.DateTimeFormatter

@Composable
fun ReviewStep(
    state: TripCreationState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "Review & Confirm",
            subtitle = "Check your trip details before creating",
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Trip Overview Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6B73FF))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = state.tripName.ifEmpty { "Untitled Trip" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.category.name.lowercase().replaceFirstChar { it.uppercase() } + " Trip",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Destination Section
                ReviewSection(
                    icon = Icons.Default.LocationOn,
                    title = "Destination",
                    content = state.destination.ifEmpty { "Not specified" }
                )

                // Agency Section
                val agencyText = if (state.selectedAgency != null) {
                    buildString {
                        append(state.selectedAgency.agencyName)
                        state.selectedAgency.agencyDescription?.let { description ->
                            append("\n$description")
                        }
                        state.selectedAgency.agencyAddress?.let { address ->
                            append("\n📍 $address")
                        }
                        state.selectedAgency.contactPhone?.let { phone ->
                            append("\n📞 $phone")
                        }
                    }
                } else {
                    "No agency selected"
                }

                ReviewSection(
                    icon = Icons.Default.Business,
                    title = "Travel Agency",
                    content = agencyText
                )

                // Bus Section
                if (state.selectedBus != null) {
                    val busText = buildString {
                        append("🚌 ${state.selectedBus.busName}")
                        append("\n📅 ${state.selectedBus.timeOfDeparture.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}")
                        append("\n🕐 Departure: ${state.selectedBus.timeOfDeparture.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                        if (state.selectedBus.destinationName.isNotEmpty()) {
                            append("\n📍 To: ${state.selectedBus.destinationName}")
                        }
                    }

                    ReviewSection(
                        icon = Icons.Default.DirectionsBus,
                        title = "Selected Bus",
                        content = busText
                    )
                }
                
                // Dates Section
                val dateText = if (state.startDate != null && state.endDate != null) {
                    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    "${state.startDate.format(formatter)} - ${state.endDate.format(formatter)}"
                } else {
                    "Dates not selected"
                }

                val durationText = if (state.startDate != null && state.endDate != null) {
                    val duration = state.endDate.toEpochDay() - state.startDate.toEpochDay()
                    " ($duration days)"
                } else ""
                
                ReviewSection(
                    icon = Icons.Default.CalendarToday,
                    title = "Travel Dates",
                    content = dateText + durationText
                )
                
                // Travelers Section
                val travelersText = buildString {
                    append("${state.companions.size + 1} traveler${if (state.companions.size + 1 > 1) "s" else ""}")
                    if (state.companions.isNotEmpty()) {
                        append("\n\nCompanions:")
                        state.companions.forEach { companion ->
                            append("\n• ${companion.name}")
                            if (companion.email.isNotEmpty()) {
                                append(" (${companion.email})")
                            }
                        }
                    }
                }
                
                ReviewSection(
                    icon = Icons.Default.Person,
                    title = "Travelers",
                    content = travelersText
                )
                
                // Budget Section
                if (state.budget > 0) {
                    ReviewSection(
                        icon = Icons.Default.AttachMoney,
                        title = "Budget",
                        content = "XAF ${state.budget} USD"
                    )
                }
                
                // Description Section
                if (state.description.isNotEmpty()) {
                    ReviewSection(
                        icon = Icons.Default.Description,
                        title = "Description",
                        content = state.description
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Confirmation Message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF6B73FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ready to create your trip?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "You can edit these details later from your trip dashboard.",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewSection(
    icon: ImageVector,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF6B73FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = content,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
