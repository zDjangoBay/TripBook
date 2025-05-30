package com.android.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R
import com.android.tripbook.ui.animation.AnimationUtils
import com.android.tripbook.ui.theme.TextPrimary
import com.android.tripbook.ui.theme.TextSecondary


/**
 * Enhanced empty state component with illustrations
 */
@Composable
fun EnhancedEmptyState(
    title: String,
    message: String,
    illustrationResId: Int,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration with animation
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
                .then(AnimationUtils.floatingAnimation(offsetY = 10, duration = 3000)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Action button
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.then(AnimationUtils.pulseAnimation(pulseFraction = 1.05f, duration = 2000))
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/**
 * Empty state for upcoming reservations
 */
@Composable
fun UpcomingReservationsEmptyState(
    onAddReservationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        title = "No Upcoming Trips",
        message = "You don't have any upcoming reservations. Ready to plan your next adventure?",
        illustrationResId = R.drawable.travel_illustration_1,
        actionText = "Book a Trip",
        onActionClick = onAddReservationClick,
        modifier = modifier
    )
}

/**
 * Empty state for past reservations
 */
@Composable
fun PastReservationsEmptyState(
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        title = "No Past Trips",
        message = "You haven't completed any trips yet. Your travel history will appear here.",
        illustrationResId = R.drawable.travel_illustration_3,
        modifier = modifier
    )
}

/**
 * Empty state for filtered reservations
 */
@Composable
fun FilteredReservationsEmptyState(
    onClearFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        title = "No Matching Trips",
        message = "No reservations match your current filters. Try adjusting your search criteria.",
        illustrationResId = R.drawable.travel_illustration_2,
        actionText = "Clear Filters",
        onActionClick = onClearFiltersClick,
        modifier = modifier
    )
}

/**
 * Empty state for calendar view
 */
@Composable
fun CalendarEmptyState(
    month: String,
    onAddReservationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        title = "No Trips in $month",
        message = "You don't have any reservations for this month. Would you like to add one?",
        illustrationResId = R.drawable.travel_illustration_4,
        actionText = "Add Reservation",
        onActionClick = onAddReservationClick,
        modifier = modifier
    )
}
