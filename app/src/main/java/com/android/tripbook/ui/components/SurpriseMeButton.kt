package com.android.tripbook.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Trip
import kotlinx.coroutines.delay

/**
 * A button that randomly selects a trip when clicked, with visual feedback:
 * - Rotating casino die icon during selection animation
 * - Uses theme's tertiary colors for visibility
 * - Disables during animation to prevent multiple clicks
 * - Automatically navigates to selected trip after animation
 *
 * @param trips List of available trips to select from
 * @param onTripSelected Callback when a random trip is selected
 * @param modifier Modifier for styling/layout
 */

@Composable
fun SurpriseMeButton(
    trips: List<Trip>,
    onTripSelected: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) 360f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "rotationAnimation"
    )

    Button(
        onClick = {
            if (!isAnimating && trips.isNotEmpty()) {
                isAnimating = true
            }
        },
        modifier = modifier,
        enabled = trips.isNotEmpty() && !isAnimating,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = "Surprise me",
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer { rotationZ = rotation }
            )
            Text(
                "Surprise Me",
                fontSize = 12.sp
            )
        }
    }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(1000)
            if (trips.isNotEmpty()) {
                val randomTrip = trips.random()
                onTripSelected(randomTrip)
            }
            isAnimating = false
        }
    }
}