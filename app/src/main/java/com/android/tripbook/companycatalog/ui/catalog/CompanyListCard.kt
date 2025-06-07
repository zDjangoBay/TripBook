/*
- This composable is a more compact alternative to CompanyCard,
  making it perfect for list-based layouts while maintaining key company details.
 */
package com.android.tripbook.companycatalog.ui.catalog

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.companycatalog.model.Company
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CompanyListCard(
    company: Company,
    onClick: () -> Unit,
    onFavoriteClick: (Boolean) -> Unit = {},
    onShareClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onFollowClick: (Boolean) -> Unit = {},
    isBookmarked: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var showQRCode by remember { mutableStateOf(false) }
    var showMetrics by remember { mutableStateOf(true) }
    var showFullDescription by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }
    
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    
    // Animation states
    val rotationAnimation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 300f)
    )
    
    val likeAnimation = remember { Animatable(1f) }
    val cardColor = remember { Animatable(Color.White.toArgb().toFloat()) }
    
    // Custom metrics
    val engagementScore = remember(company) {
        (company.likes * 0.4 + company.views * 0.3 + company.stars * 0.3).toInt()
    }

    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showQRCode = !showQRCode
                },
                onDoubleClick = {
                    scope.launch {
                        rotation += 360f
                        likeAnimation.animateTo(1.2f)
                        likeAnimation.animateTo(1f)
                    }
                }
            )
            .scale(scale)
            .graphicsLayer {
                rotationY = rotationAnimation
            }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            focusedElevation = 6.dp,
            hoveredElevation = 5.dp
        )
    ) {
        Column {
            // Premium badge
            if (company.isPremium) {
                PremiumBadge()
            }

            // Image carousel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                company.imageResIds.forEachIndexed { index, imageResId ->
                    if (index == currentImageIndex) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Image carousel controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                ) {
                    company.imageResIds.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(2.dp)
                                .background(
                                    color = if (currentImageIndex == index) Color.White else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { currentImageIndex = index }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Header section with company info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = company.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Company status chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            StatusChip(
                                icon = Icons.Filled.Verified,
                                text = if (company.isVerified) "Verified" else "Pending",
                                color = if (company.isVerified) Color.Green else Color.Gray
                            )
                            
                            if (company.isTopRated) {
                                StatusChip(
                                    icon = Icons.Filled.Star,
                                    text = "Top Rated",
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    }

                    // Quick action buttons
                    Row {
                        IconButton(
                            onClick = {
                                isFollowing = !isFollowing
                                onFollowClick(isFollowing)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFollowing) Icons.Filled.PersonAdd else Icons.Outlined.PersonAdd,
                                contentDescription = "Follow",
                                tint = if (isFollowing) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                        
                        IconButton(
                            onClick = {
                                isFavorite = !isFavorite
                                onFavoriteClick(isFavorite)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.scale(likeAnimation.value)
                            )
                        }
                    }
                }

                // Expandable description
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = if (showFullDescription) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { showFullDescription = !showFullDescription }
                )

                // Metrics section
                if (showMetrics) {
                    CompanyMetrics(company, engagementScore)
                }

                // Action buttons
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Divider()
                        
                        // Extended actions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ActionButton(Icons.Default.Share, "Share", onShareClick)
                            ActionButton(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                "Bookmark",
                                onBookmarkClick
                            )
                            ActionButton(Icons.Default.Report, "Report", onReportClick)
                            ActionButton(Icons.Default.Info, "Info") {
                                showMetrics = !showMetrics
                            }
                        }

                        // Company details
                        CompanyDetails(company)
                    }
                }

                // Expand/collapse button
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumBadge() {
    Surface(
        color = Color(0xFFFFD700),
        shape = RoundedCornerShape(bottomEnd = 8.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = "PREMIUM",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatusChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun CompanyMetrics(
    company: Company,
    engagementScore: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MetricItem(Icons.Default.ThumbUp, "${company.likes}", "Likes")
        MetricItem(Icons.Default.RemoveRedEye, "${company.views}", "Views")
        MetricItem(Icons.Default.Star, "${company.stars}/5", "Rating")
        MetricItem(Icons.Default.Trending, "$engagementScore", "Engagement")
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompanyDetails(company: Company) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        DetailItem("Founded", company.foundedDate?.let {
            SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(it)
        } ?: "N/A")
        DetailItem("Location", company.location)
        DetailItem("Industry", company.industry)
        DetailItem("Size", "${company.employeeCount} employees")
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}
