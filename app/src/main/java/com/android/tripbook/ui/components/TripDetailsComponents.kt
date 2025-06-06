package com.android.tripbook.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.ui.theme.TripBookColors
import com.android.tripbook.ui.utils.TextDefaults
import com.android.tripbook.viewmodel.TripStatistics
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TripStatisticsCard(
    statistics: TripStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TripBookColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Trip Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TripBookColors.TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.DateRange,
                    value = "${statistics.totalDays}",
                    label = "Days",
                    color = TripBookColors.Primary,
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    icon = Icons.Default.Place,
                    value = "${statistics.activitiesCount}",
                    label = "Activities",
                    color = TripBookColors.Success,
                    modifier = Modifier.weight(1f)
                )
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    value = "${statistics.completedActivities}",
                    label = "Completed",
                    color = TripBookColors.Secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TripBookColors.TextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TripBookColors.TextSecondary
        )
    }
}

@Composable
fun DateSelector(
    dates: List<LocalDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(dates) { date ->
            DateChip(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) TripBookColors.Primary else TripBookColors.Surface
    val textColor = if (isSelected) TripBookColors.TextOnPrimary else TripBookColors.TextPrimary
    
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .width(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMM")),
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.8f)
            )
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEE")),
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun DayTimelineView(
    date: LocalDate,
    activities: List<ItineraryItem>,
    onActivityClick: (ItineraryItem) -> Unit,
    onAddActivityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Date header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TripBookColors.TextPrimary
            )
            
            IconButton(
                onClick = onAddActivityClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(TripBookColors.Primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Activity",
                    tint = TripBookColors.TextOnPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        if (activities.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TripBookColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = "No activities",
                        tint = TripBookColors.TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = TextDefaults.NO_ACTIVITIES_PLANNED,
                        fontSize = 16.sp,
                        color = TripBookColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = TextDefaults.ADD_FIRST_ACTIVITY,
                        fontSize = 14.sp,
                        color = TripBookColors.TextSecondary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Activities timeline
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityTimelineCard(
                        activity = activity,
                        onClick = { onActivityClick(activity) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityTimelineCard(
    activity: ItineraryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TripBookColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Time and type indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = activity.time,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            when (activity.type) {
                                ItineraryType.ACTIVITY -> TripBookColors.ActivityColor
                                ItineraryType.ACCOMMODATION -> TripBookColors.AccommodationColor
                                ItineraryType.TRANSPORTATION -> TripBookColors.TransportationColor
                            },
                            CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.TextPrimary
                )
                if (activity.location.isNotEmpty()) {
                    Text(
                        text = activity.location,
                        fontSize = 14.sp,
                        color = TripBookColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (activity.description.isNotEmpty()) {
                    Text(
                        text = activity.description,
                        fontSize = 14.sp,
                        color = TripBookColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (activity.duration.isNotEmpty()) {
                    Text(
                        text = "Duration: ${activity.duration}",
                        fontSize = 12.sp,
                        color = TripBookColors.TextSecondary.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Completion status
            if (activity.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = TripBookColors.Success,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
