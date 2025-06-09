// ui/screens/TripDetailScreen.kt
package com.android.tripbook.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.android.tripbook.viewmodel.MockReviewViewModel
import com.android.tripbook.ui.components.ImageGallery
import com.android.tripbook.ui.components.ReviewCard
import com.android.tripbook.ui.components.MiniMap
import com.android.tripbook.data.SampleTrips
import kotlinx.coroutines.delay

data class RatingSummary(
    val averageRating: Float = 4.2f,
    val totalRatings: Int = 127,
    val ratingBreakdown: Map<Int, Int> = mapOf(
        5 to 65,
        4 to 32,
        3 to 20,
        2 to 7,
        1 to 3
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Int,
    onBack: () -> Unit,
    navController: NavHostController,
    onSeeAllReviews: (Int) -> Unit,
    onBookTrip: (Int) -> Unit = {}
) {

    val trip = remember { SampleTrips.get().find { it.id == tripId } }
    val reviewViewModel = remember { MockReviewViewModel() }
    val allReviews by reviewViewModel.reviews.collectAsState()
    val reviewsForTrip = allReviews.filter { it.tripId == tripId }

    // Rating states
    var showRatingDialog by remember { mutableStateOf(false) }
    var hasUserRated by remember { mutableStateOf(false) }
    var hasShownRatingDialog by remember { mutableStateOf(false) }
    var showRatingEffect by remember { mutableStateOf(false) }
    var ratingSummary by remember {
        mutableStateOf(RatingSummary())
    }

    // Show rating dialog after 10 seconds (only once)
    LaunchedEffect(Unit) {
        delay(10000) // 10 seconds = 10000 milliseconds
        if (!hasUserRated && !hasShownRatingDialog) {
            showRatingDialog = true
            hasShownRatingDialog = true
        }
    }

    // Rating Dialog
    if (showRatingDialog) {
        RatingDialog(
            tripName = trip?.title ?: "This Trip",
            onRatingSubmitted = { rating ->
                hasUserRated = true
                showRatingDialog = false
                showRatingEffect = true

                // Update rating summary with user's rating
                val newBreakdown = ratingSummary.ratingBreakdown.toMutableMap()
                newBreakdown[rating] = (newBreakdown[rating] ?: 0) + 1
                val newTotal = ratingSummary.totalRatings + 1
                val newAverage = (ratingSummary.averageRating * ratingSummary.totalRatings + rating) / newTotal

                ratingSummary = ratingSummary.copy(
                    averageRating = newAverage,
                    totalRatings = newTotal,
                    ratingBreakdown = newBreakdown
                )
            },
            onDismiss = { showRatingDialog = false }
        )
    }

    // Rating Success Effect
    if (showRatingEffect) {
        RatingSuccessEffect(
            onDismiss = { showRatingEffect = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (trip == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Trip not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ImageGallery(
                    images = trip.imageUrl,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { onBookTrip(tripId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Book Now")
                        }
                    }
                }

                // Rating Summary Section
                RatingSummaryCard(
                    ratingSummary = ratingSummary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = trip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                if (trip.latitude != 0.0 && trip.longitude != 0.0) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Location on Map",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MiniMap(
                            trip = trip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                } else {
                    Text(
                        text = "Location map not available for this trip.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = { onSeeAllReviews(tripId) }) {
                            Text("See All")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (reviewsForTrip.isEmpty()) {
                        Text(
                            text = "No reviews yet. Be the first to review!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        reviewsForTrip.take(3).forEach { review ->
                            ReviewCard(
                                review = review,
                                onClick = {
                                    navController.navigate("detailReview/${review.id}/$tripId")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingSuccessEffect(
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // Show for 2 seconds
        visible = false
        delay(300) // Wait for animation to finish
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.3f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(
            targetScale = 0.3f,
            animationSpec = tween(300)
        )
    ) {
        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Thank You!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Rating submitted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingSummaryCard(
    ratingSummary: RatingSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ratings & Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", ratingSummary.averageRating),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        StarRating(rating = ratingSummary.averageRating.toInt())
                    }
                    Text(
                        text = "${ratingSummary.totalRatings} ratings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    ratingSummary.ratingBreakdown.entries
                        .sortedByDescending { it.key }
                        .forEach { (stars, count) ->
                            RatingBar(
                                stars = stars,
                                count = count,
                                total = ratingSummary.totalRatings
                            )
                        }
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    stars: Int,
    count: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (total > 0) count.toFloat() / total else 0f

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$stars",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(12.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(
                        Color(0xFFFFB300),
                        RoundedCornerShape(4.dp)
                    )
            )
        }

        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
fun StarRating(
    rating: Int,
    maxStars: Int = 5,
    size: androidx.compose.ui.unit.Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(maxStars) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(size)
            )
        }
    }
}

@Composable
fun RatingDialog(
    tripName: String,
    onRatingSubmitted: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRating by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rate Your Experience",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "How was $tripName?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        Icon(
                            imageVector = if (starIndex <= selectedRating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "$starIndex stars",
                            tint = if (starIndex <= selectedRating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedRating = starIndex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Later")
                    }

                    Button(
                        onClick = {
                            if (selectedRating > 0) {
                                onRatingSubmitted(selectedRating)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedRating > 0
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}