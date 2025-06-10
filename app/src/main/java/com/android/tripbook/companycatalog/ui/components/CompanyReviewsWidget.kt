package com.android.tripbook.companycatalog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme
import java.text.SimpleDateFormat
import java.util.*

// Data classes for reviews
data class CompanyReview(
    val id: String,
    val authorName: String,
    val authorInitials: String,
    val rating: Float,
    val title: String,
    val content: String,
    val date: Date,
    val isVerified: Boolean = false,
    val helpfulCount: Int = 0,
    val category: ReviewCategory = ReviewCategory.GENERAL
)

enum class ReviewCategory(val displayName: String) {
    GENERAL("General"),
    SERVICE_QUALITY("Service Quality"),
    CUSTOMER_SUPPORT("Customer Support"),
    VALUE_FOR_MONEY("Value for Money"),
    RELIABILITY("Reliability"),
    COMMUNICATION("Communication")
}

enum class ReviewSortOption(val displayName: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    HIGHEST_RATED("Highest Rated"),
    LOWEST_RATED("Lowest Rated"),
    MOST_HELPFUL("Most Helpful")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyReviewsWidget(
    companyId: String,
    reviews: List<CompanyReview> = emptyList(),
    averageRating: Float = 0f,
    totalReviews: Int = 0,
    onWriteReview: (String) -> Unit = {},
    onReviewHelpful: (String) -> Unit = {},
    onReportReview: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ReviewCategory.GENERAL) }
    var selectedSort by remember { mutableStateOf(ReviewSortOption.NEWEST) }
    var showFilters by remember { mutableStateOf(false) }
    var expandedReviewId by remember { mutableStateOf<String?>(null) }

    val filteredAndSortedReviews = remember(reviews, selectedCategory, selectedSort) {
        val filtered = if (selectedCategory == ReviewCategory.GENERAL) {
            reviews
        } else {
            reviews.filter { it.category == selectedCategory }
        }
        
        when (selectedSort) {
            ReviewSortOption.NEWEST -> filtered.sortedByDescending { it.date }
            ReviewSortOption.OLDEST -> filtered.sortedBy { it.date }
            ReviewSortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.rating }
            ReviewSortOption.LOWEST_RATED -> filtered.sortedBy { it.rating }
            ReviewSortOption.MOST_HELPFUL -> filtered.sortedByDescending { it.helpfulCount }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with rating summary
            ReviewsSummaryHeader(
                averageRating = averageRating,
                totalReviews = totalReviews,
                onWriteReview = { onWriteReview(companyId) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter and sort controls
            ReviewControlsSection(
                selectedCategory = selectedCategory,
                selectedSort = selectedSort,
                showFilters = showFilters,
                onCategoryChange = { selectedCategory = it },
                onSortChange = { selectedSort = it },
                onToggleFilters = { showFilters = !showFilters }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reviews list
            if (filteredAndSortedReviews.isEmpty()) {
                EmptyReviewsState(selectedCategory)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAndSortedReviews) { review ->
                        ReviewCard(
                            review = review,
                            isExpanded = expandedReviewId == review.id,
                            onToggleExpanded = { 
                                expandedReviewId = if (expandedReviewId == review.id) null else review.id 
                            },
                            onHelpful = { onReviewHelpful(review.id) },
                            onReport = { onReportReview(review.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsSummaryHeader(
    averageRating: Float,
    totalReviews: Int,
    onWriteReview: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RatingStars(rating = averageRating, size = 20.dp)
                Text(
                    text = String.format("%.1f", averageRating),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "($totalReviews reviews)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = onWriteReview,
            modifier = Modifier.height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Write Review")
        }
    }
}

@Composable
private fun ReviewControlsSection(
    selectedCategory: ReviewCategory,
    selectedSort: ReviewSortOption,
    showFilters: Boolean,
    onCategoryChange: (ReviewCategory) -> Unit,
    onSortChange: (ReviewSortOption) -> Unit,
    onToggleFilters: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            @OptIn(ExperimentalMaterial3Api::class)
            FilterChip(
                onClick = onToggleFilters,
                label = { Text("Filters") },
                selected = showFilters,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                var showSortMenu by remember { mutableStateOf(false) }
                
                Box {
                    @OptIn(ExperimentalMaterial3Api::class)
                    FilterChip(
                        onClick = { showSortMenu = true },
                        label = { Text(selectedSort.displayName) },
                        selected = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        ReviewSortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.displayName) },
                                onClick = {
                                    onSortChange(option)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReviewCategory.values().take(3).forEach { category ->
                        @OptIn(ExperimentalMaterial3Api::class)
                        FilterChip(
                            onClick = { onCategoryChange(category) },
                            label = { Text(category.displayName) },
                            selected = selectedCategory == category,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    review: CompanyReview,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onHelpful: () -> Unit,
    onReport: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpanded() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Review header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Author avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.authorInitials,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = review.authorName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            if (review.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Text(
                            text = dateFormat.format(review.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                RatingStars(rating = review.rating, size = 16.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Review title
            if (review.title.isNotEmpty()) {
                Text(
                    text = review.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Review content
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )

            if (review.content.length > 150) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isExpanded) "Show less" else "Read more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onToggleExpanded() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Review actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onHelpful,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Helpful (${review.helpfulCount})")
                    }
                    
                    TextButton(
                        onClick = onReport,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Report")
                    }
                }
                
                AssistChip(
                    onClick = { },
                    label = { Text(review.category.displayName) },
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyReviewsState(selectedCategory: ReviewCategory) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = if (selectedCategory == ReviewCategory.GENERAL) {
                    "No reviews yet"
                } else {
                    "No reviews in ${selectedCategory.displayName}"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Be the first to share your experience!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun RatingStars(
    rating: Float,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = when {
                    index < rating.toInt() -> Icons.Default.Star
                    index < rating -> Icons.Default.StarHalf
                    else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = Color(0xFFFFB000)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyReviewsWidgetPreview() {
    TripBookTheme {
        CompanyReviewsWidget(
            companyId = "company_1",
            reviews = listOf(
                CompanyReview(
                    id = "review_1",
                    authorName = "John Smith",
                    authorInitials = "JS",
                    rating = 4.5f,
                    title = "Excellent Service",
                    content = "Great experience with this company. Professional staff and quality service delivery.",
                    date = Date(),
                    isVerified = true,
                    helpfulCount = 12,
                    category = ReviewCategory.SERVICE_QUALITY
                ),
                CompanyReview(
                    id = "review_2",
                    authorName = "Sarah Johnson",
                    authorInitials = "SJ",
                    rating = 5.0f,
                    title = "Outstanding Support",
                    content = "The customer support team was incredibly helpful and responsive. Highly recommend!",
                    date = Date(System.currentTimeMillis() - 86400000),
                    isVerified = false,
                    helpfulCount = 8,
                    category = ReviewCategory.CUSTOMER_SUPPORT
                )
            ),
            averageRating = 4.7f,
            totalReviews = 156
        )
    }
}
