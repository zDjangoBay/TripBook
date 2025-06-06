package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Review
import com.android.tripbook.model.ReviewSummary
import java.time.format.DateTimeFormatter

@Composable
fun RatingStars(
    rating: Float,
    maxRating: Int = 5,
    size: Int = 16,
    onRatingChanged: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(maxRating) { index ->
            val starRating = (index + 1).toFloat()
            val isFilled = rating >= starRating
            val isHalfFilled = rating >= starRating - 0.5f && rating < starRating
            
            Icon(
                imageVector = when {
                    isFilled -> Icons.Filled.Star
                    isHalfFilled -> Icons.Filled.StarHalf
                    else -> Icons.Filled.StarBorder
                },
                contentDescription = "Star $starRating",
                tint = if (isFilled || isHalfFilled) Color(0xFFFFB000) else Color(0xFFE0E0E0),
                modifier = Modifier
                    .size(size.dp)
                    .then(
                        if (onRatingChanged != null) {
                            Modifier.clickable { onRatingChanged(starRating) }
                        } else Modifier
                    )
            )
        }
    }
}

@Composable
fun ReviewSummaryCard(
    reviewSummary: ReviewSummary?,
    onWriteReviewClick: () -> Unit,
    onRateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reviews & Ratings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRateClick,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF667EEA)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate", fontSize = 12.sp)
                    }
                    
                    Button(
                        onClick = onWriteReviewClick,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667EEA)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Write Review",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Review", fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (reviewSummary != null && reviewSummary.totalReviews > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Average rating display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%.1f", reviewSummary.averageRating),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )
                        RatingStars(
                            rating = reviewSummary.averageRating,
                            size = 20
                        )
                        Text(
                            text = "${reviewSummary.totalReviews} reviews",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    // Rating distribution
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        reviewSummary.ratingDistribution.entries
                            .sortedByDescending { it.key }
                            .forEach { (stars, count) ->
                                RatingDistributionBar(
                                    stars = stars,
                                    count = count,
                                    total = reviewSummary.totalReviews
                                )
                            }
                    }
                }
            } else {
                // No reviews yet
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = "No reviews",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No reviews yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = "Be the first to share your experience!",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingDistributionBar(
    stars: Int,
    count: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (total > 0) (count.toFloat() / total.toFloat()) else 0f
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$stars",
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.width(12.dp)
        )
        
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star",
            tint = Color(0xFFFFB000),
            modifier = Modifier.size(12.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(Color(0xFFFFB000), RoundedCornerShape(4.dp))
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = count.toString(),
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.width(20.dp)
        )
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onHelpfulClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Reviewer info and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF667EEA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.userName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = review.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A202C)
                        )
                        if (review.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = review.createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                
                RatingStars(rating = review.rating, size = 16)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Review title
            if (review.title.isNotEmpty()) {
                Text(
                    text = review.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A202C)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Review content
            Text(
                text = review.content,
                fontSize = 14.sp,
                color = Color(0xFF374151),
                lineHeight = 20.sp
            )
            
            // Pros and cons
            if (review.pros.isNotEmpty() || review.cons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                if (review.pros.isNotEmpty()) {
                    ProConsList(
                        title = "Pros",
                        items = review.pros,
                        isPositive = true
                    )
                }
                
                if (review.cons.isNotEmpty()) {
                    if (review.pros.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    ProConsList(
                        title = "Cons",
                        items = review.cons,
                        isPositive = false
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Helpful button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onHelpfulClick(true) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF6B7280)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Helpful",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Helpful", fontSize = 12.sp)
                    }
                    
                    if (review.helpfulCount > 0) {
                        Text(
                            text = "${review.helpfulCount} found this helpful",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProConsList(
    title: String,
    items: List<String>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item,
                    fontSize = 12.sp,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSubmissionDialog(
    isVisible: Boolean,
    targetName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Float, title: String, content: String, pros: List<String>, cons: List<String>) -> Unit,
    isSubmitting: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        var rating by remember { mutableFloatStateOf(5f) }
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var prosText by remember { mutableStateOf("") }
        var consText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier.fillMaxWidth(0.95f)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Write a Review",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )

                    Text(
                        text = "Share your experience with $targetName",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Rating selection
                    Text(
                        text = "Overall Rating",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    RatingStars(
                        rating = rating,
                        size = 32,
                        onRatingChanged = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Review Title") },
                        placeholder = { Text("Summarize your experience") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Content
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Your Review") },
                        placeholder = { Text("Tell others about your experience...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pros
                    OutlinedTextField(
                        value = prosText,
                        onValueChange = { prosText = it },
                        label = { Text("What did you like? (Optional)") },
                        placeholder = { Text("Separate with commas") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cons
                    OutlinedTextField(
                        value = consText,
                        onValueChange = { consText = it },
                        label = { Text("What could be improved? (Optional)") },
                        placeholder = { Text("Separate with commas") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isSubmitting
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val pros = prosText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                val cons = consText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                onSubmit(rating, title, content, pros, cons)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSubmitting && title.isNotEmpty() && content.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Submit Review")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    isVisible: Boolean,
    targetName: String,
    currentRating: Float = 0f,
    onDismiss: () -> Unit,
    onSubmit: (rating: Float) -> Unit,
    isSubmitting: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        var rating by remember { mutableFloatStateOf(currentRating.takeIf { it > 0f } ?: 5f) }

        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rate Your Experience",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )

                    Text(
                        text = "How was $targetName?",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RatingStars(
                        rating = rating,
                        size = 40,
                        onRatingChanged = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (rating.toInt()) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            5 -> "Excellent"
                            else -> ""
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isSubmitting
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = { onSubmit(rating) },
                            modifier = Modifier.weight(1f),
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Submit Rating")
                            }
                        }
                    }
                }
            }
        }
    }
}
