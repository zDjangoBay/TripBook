package com.android.tripbook.datamining.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.data.feedback.RecommendationFeedback.FeedbackType
import com.android.tripbook.datamining.data.model.TravelRecommendation

/**
 * UI component for collecting feedback on recommendations
 */
@Composable
fun RecommendationFeedbackControls(
    recommendation: TravelRecommendation,
    onFeedback: (FeedbackType, Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    var liked by remember { mutableStateOf(false) }
    var disliked by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var showRating by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0f) }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Feedback buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                IconButton(
                    onClick = {
                        liked = !liked
                        if (liked) {
                            disliked = false
                            onFeedback(FeedbackType.LIKE, null)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Dislike button
                IconButton(
                    onClick = {
                        disliked = !disliked
                        if (disliked) {
                            liked = false
                            onFeedback(FeedbackType.DISLIKE, null)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (disliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                        contentDescription = "Dislike",
                        tint = if (disliked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Save button
                IconButton(
                    onClick = {
                        saved = !saved
                        if (saved) {
                            onFeedback(FeedbackType.SAVE, null)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (saved) Icons.Filled.BookmarkAdd else Icons.Outlined.BookmarkAdd,
                        contentDescription = "Save",
                        tint = if (saved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Rate button
                IconButton(
                    onClick = {
                        showRating = !showRating
                    }
                ) {
                    Icon(
                        imageVector = if (rating > 0) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Rate",
                        tint = if (rating > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Dismiss button
                IconButton(
                    onClick = {
                        onFeedback(FeedbackType.DISMISS, null)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Rating slider
            AnimatedVisibility(
                visible = showRating,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Rate this recommendation:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = rating,
                            onValueChange = { 
                                rating = it
                                onFeedback(FeedbackType.RATE, rating * 5)
                            },
                            valueRange = 0f..1f,
                            steps = 4,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = if (rating > 0) "${(rating * 5).toInt()}/5" else "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
