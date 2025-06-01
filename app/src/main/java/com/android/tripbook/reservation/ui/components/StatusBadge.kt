package com.android.tripbook.reservation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.reservation.model.ReservationStatus

/**
 * Badge component for displaying reservation status
 */
@Composable
fun StatusBadge(
    status: ReservationStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (status) {
        ReservationStatus.CONFIRMED -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.primary,
            "Confirmed"
        )
        ReservationStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.tertiary,
            "Pending"
        )
        ReservationStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.error,
            "Cancelled"
        )
        ReservationStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary,
            "Completed"
        )
        ReservationStatus.REFUNDED -> Triple(
            Color(0xFFF3E5F5).copy(alpha = 0.8f),
            Color(0xFF4A148C),
            "Refunded"
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
