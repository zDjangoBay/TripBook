package com.android.tripbook.companycatalog.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme



data class CompanyAnalytics(
    val companyId: String,
    val totalViews: Int,
    val totalContacts: Int,
    val totalQuotes: Int,
    val averageRating: Float,
    val totalReviews: Int,
    val monthlyGrowth: Float,
    val popularServices: List<ServiceAnalytics>,
    val viewsOverTime: List<ViewsData>,
    val ratingDistribution: List<RatingData>,
    val contactSources: List<ContactSourceData>
)

data class ServiceAnalytics(
    val serviceName: String,
    val views: Int,
    val inquiries: Int,
    val conversionRate: Float
)

data class ViewsData(
    val period: String,
    val views: Int
)

data class RatingData(
    val stars: Int,
    val count: Int
)

data class ContactSourceData(
    val source: String,
    val count: Int,
    val percentage: Float
)

data class MetricCard(
    val title: String,
    val value: String,
    val change: Float,
    val icon: ImageVector,
    val color: Color
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyAnalyticsDashboard(
    analytics: CompanyAnalytics,
    onMetricClick: (String) -> Unit = {},
    onExportData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("This Month") }
    var showDetailedView by remember { mutableStateOf(false) }

    val metricCards = remember(analytics) {
        listOf(
            MetricCard(
                title = "Total Views",
                value = formatNumber(analytics.totalViews),
                change = analytics.monthlyGrowth,
                icon = Icons.Default.Visibility,
                color = Color(0xFF2196F3)
            ),
            MetricCard(
                title = "Contacts",
                value = formatNumber(analytics.totalContacts),
                change = 12.5f,
                icon = Icons.Default.ContactPhone,
                color = Color(0xFF4CAF50)
            ),
            MetricCard(
                title = "Quote Requests",
                value = formatNumber(analytics.totalQuotes),
                change = 8.3f,
                icon = Icons.Default.RequestQuote,
                color = Color(0xFFFF9800)
            ),
            MetricCard(
                title = "Avg. Rating",
                value = String.format("%.1f", analytics.averageRating),
                change = 2.1f,
                icon = Icons.Default.Star,
                color = Color(0xFFFFB000)
            )
        )
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
            // Header
            AnalyticsHeader(
                selectedPeriod = selectedPeriod,
                onPeriodChange = { selectedPeriod = it },
                onExportData = onExportData,
                onToggleDetailedView = { showDetailedView = !showDetailedView }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Metric cards
            MetricCardsSection(
                metricCards = metricCards,
                onMetricClick = onMetricClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (showDetailedView) {
                // Detailed analytics sections
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Rating distribution
                    RatingDistributionSection(
                        ratingDistribution = analytics.ratingDistribution,
                        totalReviews = analytics.totalReviews
                    )

                    // Popular services
                    PopularServicesSection(
                        services = analytics.popularServices
                    )

                    // Contact sources
                    ContactSourcesSection(
                        contactSources = analytics.contactSources
                    )

                    // Views trend (simplified)
                    ViewsTrendSection(
                        viewsData = analytics.viewsOverTime
                    )
                }
            } else {
                // Summary view
                AnalyticsSummarySection(analytics = analytics)
            }
        }
    }
}

@Composable
private fun AnalyticsHeader(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    onExportData: () -> Unit,
    onToggleDetailedView: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Analytics Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Performance insights and metrics",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var showPeriodMenu by remember { mutableStateOf(false) }
            
            Box {
                @OptIn(ExperimentalMaterial3Api::class)
                FilterChip(
                    onClick = { showPeriodMenu = true },
                    label = { Text(selectedPeriod) },
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
                    expanded = showPeriodMenu,
                    onDismissRequest = { showPeriodMenu = false }
                ) {
                    listOf("This Week", "This Month", "Last 3 Months", "This Year").forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period) },
                            onClick = {
                                onPeriodChange(period)
                                showPeriodMenu = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = onToggleDetailedView,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Toggle detailed view",
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onExportData,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "Export data",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricCardsSection(
    metricCards: List<MetricCard>,
    onMetricClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(metricCards) { metric ->
            MetricCardItem(
                metric = metric,
                onClick = { onMetricClick(metric.title) }
            )
        }
    }
}

@Composable
private fun MetricCardItem(
    metric: MetricCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = metric.color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = metric.color
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = metric.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = metric.color
            )

            Text(
                text = metric.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (metric.change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = if (metric.change >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Text(
                    text = "${if (metric.change >= 0) "+" else ""}${String.format("%.1f", metric.change)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (metric.change >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RatingDistributionSection(
    ratingDistribution: List<RatingData>,
    totalReviews: Int
) {
    Column {
        Text(
            text = "Rating Distribution",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        ratingDistribution.sortedByDescending { it.stars }.forEach { rating ->
            val percentage = if (totalReviews > 0) (rating.count.toFloat() / totalReviews) * 100 else 0f
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${rating.stars}★",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(30.dp)
                )

                LinearProgressIndicator(
                    progress = percentage / 100f,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFFB000),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Text(
                    text = "${rating.count}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun PopularServicesSection(services: List<ServiceAnalytics>) {
    Column {
        Text(
            text = "Popular Services",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        services.take(3).forEach { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.serviceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${service.views} views • ${service.inquiries} inquiries",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${String.format("%.1f", service.conversionRate)}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactSourcesSection(contactSources: List<ContactSourceData>) {
    Column {
        Text(
            text = "Contact Sources",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Pie chart
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                ContactSourcesPieChart(contactSources = contactSources)
            }

            // Legend
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                contactSources.forEach { source ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(getSourceColor(source.source))
                        )
                        
                        Text(
                            text = source.source,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "${String.format("%.1f", source.percentage)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactSourcesPieChart(contactSources: List<ContactSourceData>) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val total = contactSources.sumOf { it.count }
        var startAngle = 0f
        
        contactSources.forEach { source ->
            val sweepAngle = (source.count.toFloat() / total) * 360f
            drawArc(
                color = getSourceColor(source.source),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width * 0.8f, size.height * 0.8f),
                topLeft = Offset(size.width * 0.1f, size.height * 0.1f)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
private fun ViewsTrendSection(viewsData: List<ViewsData>) {
    Column {
        Text(
            text = "Views Trend",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val maxViews = viewsData.maxOfOrNull { it.views } ?: 1
            
            viewsData.take(7).forEach { data ->
                val heightFraction = data.views.toFloat() / maxViews.toFloat()
                val height = (heightFraction * 80).dp
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(height = height)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    
                    Text(
                        text = data.period,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSummarySection(analytics: CompanyAnalytics) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Quick Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                title = "Top Service",
                value = analytics.popularServices.firstOrNull()?.serviceName ?: "N/A",
                icon = Icons.Default.Star
            )
            
            SummaryItem(
                title = "Conversion Rate",
                value = "${String.format("%.1f", analytics.popularServices.firstOrNull()?.conversionRate ?: 0f)}%",
                icon = Icons.Default.TrendingUp
            )
        }
    }
}

@Composable
private fun SummaryItem(
    title: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${String.format("%.1f", number / 1000000.0)}M"
        number >= 1000 -> "${String.format("%.1f", number / 1000.0)}K"
        else -> number.toString()
    }
}

private fun getSourceColor(source: String): Color {
    return when (source.lowercase()) {
        "website" -> Color(0xFF2196F3)
        "social media" -> Color(0xFFE91E63)
        "referral" -> Color(0xFF4CAF50)
        "direct" -> Color(0xFFFF9800)
        "search" -> Color(0xFF9C27B0)
        else -> Color(0xFF607D8B)
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyAnalyticsDashboardPreview() {
    TripBookTheme {
        CompanyAnalyticsDashboard(
            analytics = CompanyAnalytics(
                companyId = "company_1",
                totalViews = 12500,
                totalContacts = 340,
                totalQuotes = 89,
                averageRating = 4.6f,
                totalReviews = 156,
                monthlyGrowth = 15.3f,
                popularServices = listOf(
                    ServiceAnalytics("Web Development", 3200, 45, 14.1f),
                    ServiceAnalytics("UI/UX Design", 2800, 38, 13.6f),
                    ServiceAnalytics("Consulting", 1900, 22, 11.6f)
                ),
                viewsOverTime = listOf(
                    ViewsData("Mon", 1200),
                    ViewsData("Tue", 1500),
                    ViewsData("Wed", 1800),
                    ViewsData("Thu", 1600),
                    ViewsData("Fri", 2100),
                    ViewsData("Sat", 900),
                    ViewsData("Sun", 800)
                ),
                ratingDistribution = listOf(
                    RatingData(5, 89),
                    RatingData(4, 45),
                    RatingData(3, 15),
                    RatingData(2, 5),
                    RatingData(1, 2)
                ),
                contactSources = listOf(
                    ContactSourceData("Website", 150, 44.1f),
                    ContactSourceData("Social Media", 89, 26.2f),
                    ContactSourceData("Referral", 67, 19.7f),
                    ContactSourceData("Direct", 34, 10.0f)
                )
            )
        )
    }
}
