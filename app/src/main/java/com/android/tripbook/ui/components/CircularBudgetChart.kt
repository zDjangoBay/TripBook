package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.android.tripbook.ui.theme.TripBookColors

@Composable
fun CircularBudgetChart(
    progress: Float = 0.65f,
    label: String = "₣ 25,000",
    modifier: Modifier = Modifier,
    progressColor: Color = TripBookColors.ButtonPrimary,
    trackColor: Color = TripBookColors.ChipBackground
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(120.dp)
    ) {
        CircularProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            color = progressColor,
            trackColor = trackColor,
            strokeWidth = 10.dp,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TripBookColors.TextPrimary
            )
            Text(
                text = "Total Budget",
                style = MaterialTheme.typography.bodySmall,
                color = TripBookColors.TextSecondary
            )
        }
    }
}