package com.android.tripbook.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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

// Compact version for app bar
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
    
    TextButton(
        onClick = {
            if (!isAnimating && trips.isNotEmpty()) {
                isAnimating = true
            }
        },
        modifier = modifier,
        enabled = trips.isNotEmpty() && !isAnimating,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
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