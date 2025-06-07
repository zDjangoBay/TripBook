package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.R
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.Reservation
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.model.ReservationStatus
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.animation.AnimationUtils
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.util.DateUtils
import java.time.LocalDateTime

/**
 * A card displaying a reservation item in the list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationCard(
    reservation: Reservation,
    onClick: (Reservation) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                // Apply subtle 3D rotation effect on hover
                rotationX = if (isExpanded) 2f else 0f
                shadowElevation = if (isExpanded) 8f else 2f
            }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 4.dp else 2.dp
        ),
        onClick = {
            isExpanded = !isExpanded
            onClick(reservation)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Destination and status with slide-in animation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Location,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2500))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reservation.destination,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = TextPrimary
                    )
                }
                StatusIndicator(status = reservation.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title with animation
            Text(
                text = reservation.title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.animateContentSize()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date range with better icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = DateUtils.formatDateRange(reservation.startDate, reservation.endDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Accommodation with better icon
            if (reservation.accommodationName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Hotel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reservation.accommodationName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Price with better icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.Money,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${reservation.price} ${reservation.currency}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
            }

            // Expanded content with animation
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Additional details
                    if (reservation.transportInfo != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = AppIcons.Flight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reservation.transportInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Booking reference
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ref: ${reservation.bookingReference}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }

                    // Notes if available
                    if (reservation.notes != null) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = AppIcons.Notes,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reservation.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick actions with animated icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                QuickActionButton(
                    icon = AppIcons.Edit,
                    contentDescription = "Edit Reservation",
                    onClick = { /* Handle edit action */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickActionButton(
                    icon = AppIcons.Share,
                    contentDescription = "Share Reservation",
                    onClick = { /* Handle share action */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickActionButton(
                    icon = AppIcons.More,
                    contentDescription = "More Options",
                    onClick = { /* Handle more options */ }
                )
            }
        }
    }
}

/**
 * Status indicator showing the reservation status with appropriate color
 */
@Composable
fun StatusIndicator(status: ReservationStatus) {
    val backgroundColor = when (status) {
        ReservationStatus.CONFIRMED -> StatusConfirmed
        ReservationStatus.PENDING -> StatusPending
        ReservationStatus.CANCELLED -> StatusCancelled
        ReservationStatus.COMPLETED -> StatusCompleted
    }

    val statusText = status.name.lowercase().replaceFirstChar { it.uppercase() }
    val statusIcon = AppIcons.getStatusIcon(status)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor.copy(alpha = 0.1f),
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = backgroundColor,
                modifier = Modifier
                    .size(14.dp)
                    .then(AnimationUtils.pulseAnimation(pulseFraction = 1.1f, duration = 1500))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = backgroundColor
            )
        }
    }
}

/**
 * Button for quick actions on reservation cards
 */
@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .graphicsLayer {
                // Apply spring effect when pressed
                scaleX = if (isPressed) 0.9f else 1f
                scaleY = if (isPressed) 0.9f else 1f
            }
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.then(
                if (!isPressed) AnimationUtils.pulseAnimation(
                    pulseFraction = 1.1f,
                    duration = 2000
                ) else Modifier
            )
        )
    }
}

/**
 * Tab row for switching between upcoming and past reservations
 */
@Composable
fun ReservationTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {
            Divider(
                thickness = 2.dp,
                color = CardBorder.copy(alpha = 0.5f)
            )
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        // Upcoming tab with animation
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            text = {
                Text(
                    text = "Upcoming",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                    ),
                    modifier = Modifier.animateContentSize()
                )
            },
            icon = {
                Icon(
                    imageVector = AppIcons.FlightTakeoff,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .then(
                            if (selectedTabIndex == 0)
                                AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000)
                            else
                                Modifier
                        )
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.primary,
            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Past tab with animation
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            text = {
                Text(
                    text = "Past",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                    ),
                    modifier = Modifier.animateContentSize()
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .then(
                            if (selectedTabIndex == 1)
                                AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000)
                            else
                                Modifier
                        )
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.primary,
            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
