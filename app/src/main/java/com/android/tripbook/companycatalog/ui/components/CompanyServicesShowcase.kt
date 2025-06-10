package com.android.tripbook.companycatalog.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme

// Data classes for services
data class CompanyService(
    val id: String,
    val name: String,
    val description: String,
    val category: ServiceCategory,
    val price: ServicePrice?,
    val duration: String?,
    val availability: ServiceAvailability,
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0
)

data class ServicePrice(
    val amount: Double,
    val currency: String = "USD",
    val unit: PriceUnit = PriceUnit.FIXED
)

enum class ServiceCategory(val displayName: String, val icon: ImageVector) {
    CONSULTING("Consulting", Icons.Default.Psychology),
    DEVELOPMENT("Development", Icons.Default.Code),
    DESIGN("Design", Icons.Default.Palette),
    MARKETING("Marketing", Icons.Default.Campaign),
    SUPPORT("Support", Icons.Default.Support),
    TRAINING("Training", Icons.Default.School),
    MAINTENANCE("Maintenance", Icons.Default.Build),
    OTHER("Other", Icons.Default.Category)
}

enum class ServiceAvailability(val displayName: String, val color: Color) {
    AVAILABLE("Available", Color(0xFF4CAF50)),
    LIMITED("Limited", Color(0xFFFF9800)),
    UNAVAILABLE("Unavailable", Color(0xFFF44336)),
    ON_REQUEST("On Request", Color(0xFF2196F3))
}

enum class PriceUnit(val displayName: String) {
    FIXED("Fixed Price"),
    HOURLY("Per Hour"),
    DAILY("Per Day"),
    MONTHLY("Per Month"),
    PROJECT("Per Project")
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyServicesShowcase(
    companyId: String,
    services: List<CompanyService> = emptyList(),
    onServiceClick: (String) -> Unit = {},
    onRequestQuote: (String) -> Unit = {},
    onContactForService: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }
    var showAllServices by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }

    val filteredServices = remember(services, selectedCategory) {
        if (selectedCategory == null) {
            services
        } else {
            services.filter { it.category == selectedCategory }
        }
    }

    val displayedServices = remember(filteredServices, showAllServices) {
        if (showAllServices) filteredServices else filteredServices.take(6)
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
            ServicesHeader(
                totalServices = services.size,
                viewMode = viewMode,
                onViewModeChange = { viewMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category filters
            ServiceCategoryFilters(
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it },
                services = services
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Services display
            if (displayedServices.isEmpty()) {
                EmptyServicesState(selectedCategory)
            } else {
                when (viewMode) {
                    ViewMode.GRID -> {
                        ServicesGrid(
                            services = displayedServices,
                            onServiceClick = onServiceClick,
                            onRequestQuote = onRequestQuote
                        )
                    }
                    ViewMode.LIST -> {
                        ServicesList(
                            services = displayedServices,
                            onServiceClick = onServiceClick,
                            onRequestQuote = onRequestQuote,
                            onContactForService = onContactForService
                        )
                    }
                }

                // Show more/less button
                if (filteredServices.size > 6) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = { showAllServices = !showAllServices }
                        ) {
                            Text(
                                text = if (showAllServices) {
                                    "Show Less"
                                } else {
                                    "Show All ${filteredServices.size} Services"
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val rotation by animateFloatAsState(
                                targetValue = if (showAllServices) 180f else 0f,
                                animationSpec = tween(300)
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .rotate(rotation)
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class ViewMode(val displayName: String, val icon: ImageVector) {
    GRID("Grid", Icons.Default.GridView),
    LIST("List", Icons.Default.ViewList)
}

@Composable
private fun ServicesHeader(
    totalServices: Int,
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Services Offered",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$totalServices services available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ViewMode.values().forEach { mode ->
                IconButton(
                    onClick = { onViewModeChange(mode) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (viewMode == mode) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            } else {
                                Color.Transparent
                            }
                        )
                ) {
                    Icon(
                        imageVector = mode.icon,
                        contentDescription = mode.displayName,
                        modifier = Modifier.size(20.dp),
                        tint = if (viewMode == mode) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCategoryFilters(
    selectedCategory: ServiceCategory?,
    onCategorySelect: (ServiceCategory?) -> Unit,
    services: List<CompanyService>
) {
    val categoriesWithCounts = remember(services) {
        ServiceCategory.values().mapNotNull { category ->
            val count = services.count { it.category == category }
            if (count > 0) category to count else null
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            @OptIn(ExperimentalMaterial3Api::class)
            FilterChip(
                onClick = { onCategorySelect(null) },
                label = { Text("All (${services.size})") },
                selected = selectedCategory == null
            )
        }
        
        items(categoriesWithCounts) { (category, count) ->
            @OptIn(ExperimentalMaterial3Api::class)
            FilterChip(
                onClick = { onCategorySelect(category) },
                label = { Text("${category.displayName} ($count)") },
                selected = selectedCategory == category,
                leadingIcon = {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun ServicesGrid(
    services: List<CompanyService>,
    onServiceClick: (String) -> Unit,
    onRequestQuote: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(services) { service ->
            ServiceGridCard(
                service = service,
                onClick = { onServiceClick(service.id) },
                onRequestQuote = { onRequestQuote(service.id) }
            )
        }
    }
}

@Composable
private fun ServicesList(
    services: List<CompanyService>,
    onServiceClick: (String) -> Unit,
    onRequestQuote: (String) -> Unit,
    onContactForService: (String) -> Unit
) {
    Column(
        modifier = Modifier.heightIn(max = 400.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        services.forEach { service ->
            ServiceListCard(
                service = service,
                onClick = { onServiceClick(service.id) },
                onRequestQuote = { onRequestQuote(service.id) },
                onContact = { onContactForService(service.id) }
            )
        }
    }
}

@Composable
private fun ServiceGridCard(
    service: CompanyService,
    onClick: () -> Unit,
    onRequestQuote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Service header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = service.category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                if (service.isPopular) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Popular") },
                        modifier = Modifier.height(24.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Service name
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Service description
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price and availability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    if (service.price != null) {
                        Text(
                            text = "${service.price.currency} ${service.price.amount}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = service.price.unit.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Quote on request",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(service.availability.color.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = service.availability.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = service.availability.color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action button
            Button(
                onClick = onRequestQuote,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("Get Quote", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ServiceListCard(
    service: CompanyService,
    onClick: () -> Unit,
    onRequestQuote: () -> Unit,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Service icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = service.category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Service details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (service.isPopular) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Popular") },
                                modifier = Modifier.height(20.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    labelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (service.price != null) {
                            Text(
                                text = "${service.price.currency} ${service.price.amount}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(service.availability.color.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = service.availability.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = service.availability.color,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Button(
                    onClick = onRequestQuote,
                    modifier = Modifier.width(100.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Quote", style = MaterialTheme.typography.bodySmall)
                }
                
                OutlinedButton(
                    onClick = onContact,
                    modifier = Modifier.width(100.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Contact", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun EmptyServicesState(selectedCategory: ServiceCategory?) {
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
                imageVector = Icons.Default.BusinessCenter,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = if (selectedCategory == null) {
                    "No services available"
                } else {
                    "No ${selectedCategory.displayName.lowercase()} services"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Check back later for updates",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyServicesShowcasePreview() {
    TripBookTheme {
        CompanyServicesShowcase(
            companyId = "company_1",
            services = listOf(
                CompanyService(
                    id = "service_1",
                    name = "Web Development",
                    description = "Custom web application development using modern technologies",
                    category = ServiceCategory.DEVELOPMENT,
                    price = ServicePrice(2500.0, "USD", PriceUnit.PROJECT),
                    duration = "4-6 weeks",
                    availability = ServiceAvailability.AVAILABLE,
                    isPopular = true,
                    rating = 4.8f,
                    reviewCount = 24
                ),
                CompanyService(
                    id = "service_2",
                    name = "UI/UX Design",
                    description = "User interface and experience design for web and mobile applications",
                    category = ServiceCategory.DESIGN,
                    price = ServicePrice(150.0, "USD", PriceUnit.HOURLY),
                    duration = "2-3 weeks",
                    availability = ServiceAvailability.LIMITED,
                    rating = 4.9f,
                    reviewCount = 18
                ),
                CompanyService(
                    id = "service_3",
                    name = "Business Consulting",
                    description = "Strategic business consulting and process optimization",
                    category = ServiceCategory.CONSULTING,
                    price = null,
                    duration = "Varies",
                    availability = ServiceAvailability.ON_REQUEST,
                    rating = 4.7f,
                    reviewCount = 31
                )
            )
        )
    }
}
