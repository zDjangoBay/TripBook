// RatingComponents.kt
package com.android.tripbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Interactive Star Rating Component for user input
 */
@Composable
fun StarRatingInput(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: Int = 32
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxRating) { index ->
            val isSelected = index < rating
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Star ${index + 1}",
                tint = if (isSelected) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable { onRatingChanged(index + 1) }
                    .clip(CircleShape)
                    .padding(4.dp)
            )
        }
    }
}

/**
 * Display-only Star Rating Component for showing existing ratings
 */
@Composable
fun StarRatingDisplay(
    rating: Float,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: Int = 20,
    showRatingText: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxRating) { index ->
            val isFullStar = index < rating.toInt()
            val isHalfStar = index == rating.toInt() && rating % 1 != 0f

            Icon(
                imageVector = if (isFullStar || isHalfStar) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (isFullStar || isHalfStar) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(starSize.dp)
            )
        }

        if (showRatingText) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Comprehensive Rating Summary Component
 */
@Composable
fun RatingSummary(
    averageRating: Float,
    totalReviews: Int,
    ratingBreakdown: Map<Int, Int>, // Map of rating (1-5) to count
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = String.format("%.1f", averageRating),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    StarRatingDisplay(
                        rating = averageRating,
                        starSize = 24,
                        showRatingText = false
                    )
                    Text(
                        text = "$totalReviews reviews",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // Rating breakdown
                Column(
                    modifier = Modifier.weight(1f).padding(start = 24.dp)
                ) {
                    (5 downTo 1).forEach { stars ->
                        val count = ratingBreakdown[stars] ?: 0
                        val percentage = if (totalReviews > 0) count.toFloat() / totalReviews else 0f

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$stars",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.width(16.dp)
                            )
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            LinearProgressIndicator(
                                progress = percentage,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp),
                                color = Color(0xFFFFD700),
                                trackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.width(24.dp)
                            )
                        }
                        if (stars > 1) Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}