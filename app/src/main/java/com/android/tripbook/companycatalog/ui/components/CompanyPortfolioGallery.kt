package com.android.tripbook.companycatalog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.text.SimpleDateFormat
import java.util.*

// Data classes for portfolio
data class PortfolioProject(
    val id: String,
    val title: String,
    val description: String,
    val category: ProjectCategory,
    val imageUrl: String?,
    val clientName: String,
    val completionDate: Date,
    val duration: String,
    val technologies: List<String>,
    val projectValue: String?,
    val isFeature: Boolean = false,
    val tags: List<String> = emptyList(),
    val testimonial: ProjectTestimonial? = null,
    val metrics: ProjectMetrics? = null
)

data class ProjectTestimonial(
    val clientName: String,
    val clientTitle: String,
    val content: String,
    val rating: Float
)

data class ProjectMetrics(
    val performanceImprovement: String?,
    val userGrowth: String?,
    val revenueIncrease: String?,
    val timeToMarket: String?
)

enum class ProjectCategory(val displayName: String, val icon: ImageVector, val color: Color) {
    WEB_DEVELOPMENT("Web Development", Icons.Default.Language, Color(0xFF2196F3)),
    MOBILE_APP("Mobile App", Icons.Default.PhoneAndroid, Color(0xFF4CAF50)),
    UI_UX_DESIGN("UI/UX Design", Icons.Default.Palette, Color(0xFFE91E63)),
    BRANDING("Branding", Icons.Default.Brush, Color(0xFFFF9800)),
    E_COMMERCE("E-Commerce", Icons.Default.ShoppingCart, Color(0xFF9C27B0)),
    CONSULTING("Consulting", Icons.Default.Psychology, Color(0xFF607D8B)),
    MARKETING("Digital Marketing", Icons.Default.Campaign, Color(0xFFFF5722)),
    OTHER("Other", Icons.Default.Category, Color(0xFF795548))
}

enum class PortfolioViewMode(val displayName: String, val icon: ImageVector) {
    GRID("Grid", Icons.Default.GridView),
    LIST("List", Icons.Default.ViewList),
    FEATURED("Featured", Icons.Default.Star)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPortfolioGallery(
    companyId: String,
    projects: List<PortfolioProject> = emptyList(),
    onProjectClick: (String) -> Unit = {},
    onCategoryFilter: (ProjectCategory?) -> Unit = {},
    onContactForProject: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<ProjectCategory?>(null) }
    var viewMode by remember { mutableStateOf(PortfolioViewMode.GRID) }
    var showFilters by remember { mutableStateOf(false) }
    var expandedProjectId by remember { mutableStateOf<String?>(null) }

    val filteredProjects = remember(projects, selectedCategory) {
        if (selectedCategory == null) {
            projects
        } else {
            projects.filter { it.category == selectedCategory }
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
            // Header
            PortfolioHeader(
                totalProjects = projects.size,
                viewMode = viewMode,
                onViewModeChange = { viewMode = it },
                onToggleFilters = { showFilters = !showFilters }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    PortfolioFilters(
                        selectedCategory = selectedCategory,
                        onCategorySelect = {
                            selectedCategory = it
                            onCategoryFilter(it)
                        },
                        projects = projects
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Portfolio content
            if (filteredProjects.isEmpty()) {
                EmptyPortfolioState(selectedCategory)
            } else {
                when (viewMode) {
                    PortfolioViewMode.GRID -> {
                        PortfolioGrid(
                            projects = filteredProjects,
                            onProjectClick = onProjectClick,
                            onContactForProject = onContactForProject
                        )
                    }
                    PortfolioViewMode.LIST -> {
                        PortfolioList(
                            projects = filteredProjects,
                            expandedProjectId = expandedProjectId,
                            onProjectClick = onProjectClick,
                            onToggleExpanded = { projectId ->
                                expandedProjectId = if (expandedProjectId == projectId) null else projectId
                            },
                            onContactForProject = onContactForProject
                        )
                    }
                    PortfolioViewMode.FEATURED -> {
                        FeaturedProjects(
                            projects = filteredProjects.filter { it.isFeature },
                            onProjectClick = onProjectClick,
                            onContactForProject = onContactForProject
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioHeader(
    totalProjects: Int,
    viewMode: PortfolioViewMode,
    onViewModeChange: (PortfolioViewMode) -> Unit,
    onToggleFilters: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Portfolio Gallery",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$totalProjects projects completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onToggleFilters,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Toggle filters",
                    modifier = Modifier.size(20.dp)
                )
            }

            PortfolioViewMode.values().forEach { mode ->
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
private fun PortfolioFilters(
    selectedCategory: ProjectCategory?,
    onCategorySelect: (ProjectCategory?) -> Unit,
    projects: List<PortfolioProject>
) {
    val categoriesWithCounts = remember(projects) {
        ProjectCategory.values().mapNotNull { category ->
            val count = projects.count { it.category == category }
            if (count > 0) category to count else null
        }
    }

    Column {
        Text(
            text = "Filter by Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                @OptIn(ExperimentalMaterial3Api::class)
                FilterChip(
                    onClick = { onCategorySelect(null) },
                    label = { Text("All (${projects.size})") },
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
                            modifier = Modifier.size(16.dp),
                            tint = category.color
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PortfolioGrid(
    projects: List<PortfolioProject>,
    onProjectClick: (String) -> Unit,
    onContactForProject: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.heightIn(max = 500.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects) { project ->
            ProjectGridCard(
                project = project,
                onClick = { onProjectClick(project.id) },
                onContact = { onContactForProject(project.id) }
            )
        }
    }
}

@Composable
private fun ProjectGridCard(
    project: PortfolioProject,
    onClick: () -> Unit,
    onContact: () -> Unit
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
            // Project header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = project.category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = project.category.color
                )

                if (project.isFeature) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Featured") },
                        modifier = Modifier.height(24.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Project title
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Project description
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Client and date
            Text(
                text = "Client: ${project.clientName}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )

            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            Text(
                text = "Completed: ${dateFormat.format(project.completionDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Technologies
            if (project.technologies.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(project.technologies.take(3)) { tech ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tech) },
                            modifier = Modifier.height(24.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = project.category.color.copy(alpha = 0.1f),
                                labelColor = project.category.color
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action button
            Button(
                onClick = onContact,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("Discuss Similar Project", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun PortfolioList(
    projects: List<PortfolioProject>,
    expandedProjectId: String?,
    onProjectClick: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onContactForProject: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 500.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects) { project ->
            ProjectListCard(
                project = project,
                isExpanded = expandedProjectId == project.id,
                onClick = { onProjectClick(project.id) },
                onToggleExpanded = { onToggleExpanded(project.id) },
                onContact = { onContactForProject(project.id) }
            )
        }
    }
}

@Composable
private fun ProjectListCard(
    project: PortfolioProject,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onToggleExpanded: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Project header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(project.category.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = project.category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = project.category.color
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (project.isFeature) {
                                AssistChip(
                                    onClick = { },
                                    label = { Text("Featured") },
                                    modifier = Modifier.height(20.dp),
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        labelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        Text(
                            text = "${project.category.displayName} • ${project.clientName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onToggleExpanded,
                    modifier = Modifier.size(32.dp)
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(300)
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotation)
                    )
                }
            }

            // Expanded content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Description
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Project details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = project.duration,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (project.projectValue != null) {
                            Column {
                                Text(
                                    text = "Project Value",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = project.projectValue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Technologies
                    if (project.technologies.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Technologies Used",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(project.technologies) { tech ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(tech) },
                                        modifier = Modifier.height(28.dp),
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = project.category.color.copy(alpha = 0.1f),
                                            labelColor = project.category.color
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Testimonial
                    project.testimonial?.let { testimonial ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "\"${testimonial.content}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${testimonial.clientName}, ${testimonial.clientTitle}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        repeat(5) { index ->
                                            Icon(
                                                imageVector = if (index < testimonial.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color(0xFFFFB000)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onContact,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discuss Similar Project")
                        }

                        OutlinedButton(
                            onClick = onClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Details")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedProjects(
    projects: List<PortfolioProject>,
    onProjectClick: (String) -> Unit,
    onContactForProject: (String) -> Unit
) {
    if (projects.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "No featured projects yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 500.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(projects) { project ->
                FeaturedProjectCard(
                    project = project,
                    onClick = { onProjectClick(project.id) },
                    onContact = { onContactForProject(project.id) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedProjectCard(
    project: PortfolioProject,
    onClick: () -> Unit,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Featured badge and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Featured Project")
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )

                AssistChip(
                    onClick = { },
                    label = { Text(project.category.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = project.category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = project.category.color
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = project.category.color.copy(alpha = 0.1f),
                        labelColor = project.category.color
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Project title and description
            Text(
                text = project.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Project details grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProjectDetailItem(
                    label = "Client",
                    value = project.clientName,
                    modifier = Modifier.weight(1f)
                )

                ProjectDetailItem(
                    label = "Duration",
                    value = project.duration,
                    modifier = Modifier.weight(1f)
                )

                if (project.projectValue != null) {
                    ProjectDetailItem(
                        label = "Value",
                        value = project.projectValue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Metrics if available
            project.metrics?.let { metrics ->
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Project Impact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    metrics.performanceImprovement?.let {
                        MetricItem(
                            label = "Performance",
                            value = it,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    metrics.userGrowth?.let {
                        MetricItem(
                            label = "User Growth",
                            value = it,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    metrics.revenueIncrease?.let {
                        MetricItem(
                            label = "Revenue",
                            value = it,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Technologies
            if (project.technologies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Technologies",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(project.technologies) { tech ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tech) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = project.category.color.copy(alpha = 0.1f),
                                labelColor = project.category.color
                            )
                        )
                    }
                }
            }

            // Testimonial
            project.testimonial?.let { testimonial ->
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Client Testimonial",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "\"${testimonial.content}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${testimonial.clientName}, ${testimonial.clientTitle}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < testimonial.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color(0xFFFFB000)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onContact,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContactMail,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Discuss Similar Project")
                }

                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Full Case Study")
                }
            }
        }
    }
}

@Composable
private fun ProjectDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyPortfolioState(selectedCategory: ProjectCategory?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Text(
                text = if (selectedCategory == null) {
                    "No portfolio projects yet"
                } else {
                    "No ${selectedCategory.displayName.lowercase()} projects"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Portfolio projects will be displayed here once available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyPortfolioGalleryPreview() {
    TripBookTheme {
        CompanyPortfolioGallery(
            companyId = "company_1",
            projects = listOf(
                PortfolioProject(
                    id = "project_1",
                    title = "E-Commerce Platform Redesign",
                    description = "Complete redesign and development of a modern e-commerce platform with improved user experience and performance optimization.",
                    category = ProjectCategory.WEB_DEVELOPMENT,
                    imageUrl = null,
                    clientName = "RetailCorp Inc.",
                    completionDate = Date(System.currentTimeMillis() - 86400000 * 30),
                    duration = "4 months",
                    technologies = listOf("React", "Node.js", "MongoDB", "AWS"),
                    projectValue = "$75,000",
                    isFeature = true,
                    testimonial = ProjectTestimonial(
                        clientName = "Sarah Johnson",
                        clientTitle = "CTO",
                        content = "Exceptional work! The new platform increased our conversion rate by 40% and significantly improved user engagement.",
                        rating = 5.0f
                    ),
                    metrics = ProjectMetrics(
                        performanceImprovement = "+65%",
                        userGrowth = "+40%",
                        revenueIncrease = "+35%",
                        timeToMarket = "2 weeks early"
                    )
                ),
                PortfolioProject(
                    id = "project_2",
                    title = "Mobile Banking App",
                    description = "Secure and user-friendly mobile banking application with advanced features and biometric authentication.",
                    category = ProjectCategory.MOBILE_APP,
                    imageUrl = null,
                    clientName = "SecureBank",
                    completionDate = Date(System.currentTimeMillis() - 86400000 * 60),
                    duration = "6 months",
                    technologies = listOf("Flutter", "Firebase", "Biometrics"),
                    projectValue = "$120,000",
                    isFeature = false
                ),
                PortfolioProject(
                    id = "project_3",
                    title = "Brand Identity Design",
                    description = "Complete brand identity design including logo, color palette, typography, and brand guidelines.",
                    category = ProjectCategory.BRANDING,
                    imageUrl = null,
                    clientName = "StartupXYZ",
                    completionDate = Date(System.currentTimeMillis() - 86400000 * 90),
                    duration = "2 months",
                    technologies = listOf("Adobe Creative Suite", "Figma"),
                    projectValue = "$25,000",
                    isFeature = false
                )
            )
        )
    }
}