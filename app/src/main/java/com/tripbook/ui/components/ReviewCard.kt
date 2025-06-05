package com.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripbook.models.Review
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReviewCard(
    review: Review,
    onLikeClick: () -> Unit,
    onHelpfulClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colors.primary
                    )
                    Text(
                        text = review.rating.toString(),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.content,
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(review.createdAt),
                    style = MaterialTheme.typography.caption
                )

                Row {
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like",
                            tint = if (review.likes > 0) MaterialTheme.colors.primary 
                                  else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = review.likes.toString(),
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            if (review.isVerifiedTrip) {
                Chip(
                    onClick = onHelpfulClick,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Helpful (${review.helpfulVotes})",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
} 