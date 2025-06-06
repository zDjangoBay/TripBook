package com.android.tripbook.ui.uis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.ui.components.*
import com.android.tripbook.viewmodel.ReviewViewModel

@Composable
fun CommunityReviewsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reviewViewModel: ReviewViewModel = viewModel()
    val uiState by reviewViewModel.uiState.collectAsState()
    
    // Load recent reviews when screen opens
    LaunchedEffect(Unit) {
        reviewViewModel.loadRecentReviews()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Community Reviews",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "See what fellow travelers are saying",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            
            // Content
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Filter chips (future enhancement)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = true,
                                onClick = { /* TODO: Filter by all */ },
                                label = { Text("All Reviews") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { /* TODO: Filter by trips */ },
                                label = { Text("Trips") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { /* TODO: Filter by agencies */ },
                                label = { Text("Agencies") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { /* TODO: Filter by destinations */ },
                                label = { Text("Destinations") }
                            )
                        }
                    }
                    
                    // Reviews list
                    if (uiState.reviews.isNotEmpty()) {
                        items(uiState.reviews) { review ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    // Review type badge
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = when (review.reviewType.name) {
                                                    "TRIP" -> Color(0xFFE6F3FF)
                                                    "AGENCY" -> Color(0xFFE6FFE6)
                                                    "DESTINATION" -> Color(0xFFFFF0E6)
                                                    "ACTIVITY" -> Color(0xFFFFE6F3)
                                                    else -> Color(0xFFF5F5F5)
                                                }
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = review.reviewType.name.lowercase().replaceFirstChar { it.uppercase() },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = when (review.reviewType.name) {
                                                    "TRIP" -> Color(0xFF0066CC)
                                                    "AGENCY" -> Color(0xFF00CC66)
                                                    "DESTINATION" -> Color(0xFFFF9500)
                                                    "ACTIVITY" -> Color(0xFFE91E63)
                                                    else -> Color(0xFF666666)
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        
                                        Text(
                                            text = review.targetName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Review content
                                    ReviewCard(
                                        review = review,
                                        onHelpfulClick = { isHelpful ->
                                            reviewViewModel.markReviewHelpful(review.id, isHelpful)
                                        }
                                    )
                                }
                            }
                        }
                    } else if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF667EEA)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Loading community reviews...",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            // Empty state
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RateReview,
                                    contentDescription = "No reviews",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No reviews yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = "Be the first to share your travel experience!",
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    
                    // Error state
                    uiState.error?.let { error ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = "Error",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Error loading reviews",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                    Text(
                                        text = error,
                                        fontSize = 14.sp,
                                        color = Color(0xFF7F1D1D),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { 
                                            reviewViewModel.clearError()
                                            reviewViewModel.loadRecentReviews()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFDC2626)
                                        )
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
