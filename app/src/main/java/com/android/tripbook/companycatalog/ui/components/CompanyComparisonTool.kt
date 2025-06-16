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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.android.tripbook.ui.theme.TripBookTheme

data class ComparisonCompany(
    val id: String,
    val name: String,
    val logoResId: Int,
    val rating: Float,
    val reviewCount: Int,
    val priceRange: PriceRange,
    val location: String,
    val specialties: List<String>,
    val yearsInBusiness: Int,
    val teamSize: String,
    val responseTime: String,
    val certifications: List<String>,
    val portfolioCount: Int,
    val clientSatisfaction: Float
)

data class ComparisonCriterion(
    val id: String,
    val name: String,
    val weight: Float = 1.0f,
    val type: CriterionType = CriterionType.SCORE,
    val icon: ImageVector
)

enum class PriceRange(val displayName: String, val color: Color) {
    BUDGET("Budget", Color(0xFF4CAF50)),
    MODERATE("Moderate", Color(0xFF2196F3)),
    PREMIUM("Premium", Color(0xFFFF9800)),
    LUXURY("Luxury", Color(0xFF9C27B0))
}

enum class CriterionType {
    SCORE, RATING, TEXT, COUNT, PERCENTAGE
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyComparisonTool(
    availableCompanies: List<ComparisonCompany> = emptyList(),
    selectedCompanies: List<String> = emptyList(),
    onCompanySelect: (String) -> Unit = {},
    onCompanyRemove: (String) -> Unit = {},
    onClearComparison: () -> Unit = {},
    onContactCompany: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCompanySelector by remember { mutableStateOf(false) }
    var selectedCriteria by remember { mutableStateOf(getDefaultCriteria()) }

    val companiesToCompare = availableCompanies.filter { it.id in selectedCompanies }

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
            ComparisonHeader(
                selectedCount = selectedCompanies.size,
                onAddCompany = { showCompanySelector = true },
                onClearAll = onClearComparison
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (companiesToCompare.isEmpty()) {
                EmptyComparisonState(onAddCompany = { showCompanySelector = true })
            } else {
                // Selected companies chips
                SelectedCompaniesRow(
                    companies = companiesToCompare,
                    onRemoveCompany = onCompanyRemove
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Comparison criteria selector
                CriteriaSelector(
                    selectedCriteria = selectedCriteria,
                    onCriteriaChange = { selectedCriteria = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Comparison table
                ComparisonTable(
                    companies = companiesToCompare,
                    criteria = selectedCriteria,
                    onContactCompany = onContactCompany
                )
            }

            // Company selector modal
            AnimatedVisibility(
                visible = showCompanySelector,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                CompanySelectorModal(
                    availableCompanies = availableCompanies,
                    selectedCompanies = selectedCompanies,
                    onCompanySelect = onCompanySelect,
                    onDismiss = { showCompanySelector = false }
                )
            }
        }
    }
}

@Composable
private fun ComparisonHeader(
    selectedCount: Int,
    onAddCompany: () -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Company Comparison",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$selectedCount companies selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAddCompany,
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Company")
            }

            if (selectedCount > 0) {
                OutlinedButton(
                    onClick = onClearAll,
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
private fun EmptyComparisonState(onAddCompany: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Compare,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Text(
                text = "Start Comparing Companies",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Add companies to compare their features, pricing, and services side by side",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Button(onClick = onAddCompany) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add First Company")
            }
        }
    }
}

@Composable
private fun SelectedCompaniesRow(
    companies: List<ComparisonCompany>,
    onRemoveCompany: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(companies) { company ->
            CompanyChip(
                company = company,
                onRemove = { onRemoveCompany(company.id) }
            )
        }
    }
}

@Composable
private fun CompanyChip(
    company: ComparisonCompany,
    onRemove: () -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    FilterChip(
        onClick = { },
        label = { 
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = company.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        },
        selected = true,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            selectedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun CriteriaSelector(
    selectedCriteria: List<ComparisonCriterion>,
    onCriteriaChange: (List<ComparisonCriterion>) -> Unit
) {
    val allCriteria = getAllCriteria()
    
    Column {
        Text(
            text = "Comparison Criteria",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCriteria) { criterion ->
                val isSelected = selectedCriteria.any { it.id == criterion.id }
                
                @OptIn(ExperimentalMaterial3Api::class)
                FilterChip(
                    onClick = {
                        val newCriteria = if (isSelected) {
                            selectedCriteria.filter { it.id != criterion.id }
                        } else {
                            selectedCriteria + criterion
                        }
                        onCriteriaChange(newCriteria)
                    },
                    label = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = criterion.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(criterion.name)
                        }
                    },
                    selected = isSelected
                )
            }
        }
    }
}

@Composable
private fun ComparisonTable(
    companies: List<ComparisonCompany>,
    criteria: List<ComparisonCriterion>,
    onContactCompany: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 400.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header row
        item {
            ComparisonHeaderRow(companies = companies)
        }
        
        // Criteria rows
        items(criteria) { criterion ->
            ComparisonCriterionRow(
                criterion = criterion,
                companies = companies
            )
        }
        
        // Action row
        item {
            ComparisonActionRow(
                companies = companies,
                onContactCompany = onContactCompany
            )
        }
    }
}

@Composable
private fun ComparisonHeaderRow(companies: List<ComparisonCompany>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            companies.forEach { company ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = company.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonCriterionRow(
    criterion: ComparisonCriterion,
    companies: List<ComparisonCompany>
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Criterion name
            Row(
                modifier = Modifier.weight(0.3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = criterion.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = criterion.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Company values
            Row(
                modifier = Modifier.weight(0.7f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                companies.forEach { company ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getCriterionValue(criterion, company),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonActionRow(
    companies: List<ComparisonCompany>,
    onContactCompany: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            companies.forEach { company ->
                Button(
                    onClick = { onContactCompany(company.id) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = "Contact",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanySelectorModal(
    availableCompanies: List<ComparisonCompany>,
    selectedCompanies: List<String>,
    onCompanySelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Companies to Compare",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableCompanies.filter { it.id !in selectedCompanies }) { company ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onCompanySelect(company.id)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = company.name.take(2).uppercase(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = company.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${company.rating}★ • ${company.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper functions
private fun getDefaultCriteria(): List<ComparisonCriterion> {
    return listOf(
        ComparisonCriterion("rating", "Rating", 1.0f, CriterionType.RATING, Icons.Default.Star),
        ComparisonCriterion("price", "Price Range", 1.0f, CriterionType.TEXT, Icons.Default.AttachMoney),
        ComparisonCriterion("experience", "Years in Business", 1.0f, CriterionType.COUNT, Icons.Default.Business)
    )
}

private fun getAllCriteria(): List<ComparisonCriterion> {
    return listOf(
        ComparisonCriterion("rating", "Rating", 1.0f, CriterionType.RATING, Icons.Default.Star),
        ComparisonCriterion("price", "Price Range", 1.0f, CriterionType.TEXT, Icons.Default.AttachMoney),
        ComparisonCriterion("experience", "Years in Business", 1.0f, CriterionType.COUNT, Icons.Default.Business),
        ComparisonCriterion("team", "Team Size", 1.0f, CriterionType.TEXT, Icons.Default.Group),
        ComparisonCriterion("response", "Response Time", 1.0f, CriterionType.TEXT, Icons.Default.Schedule),
        ComparisonCriterion("portfolio", "Portfolio Projects", 1.0f, CriterionType.COUNT, Icons.Default.Work),
        ComparisonCriterion("satisfaction", "Client Satisfaction", 1.0f, CriterionType.PERCENTAGE, Icons.Default.ThumbUp)
    )
}

private fun getCriterionValue(criterion: ComparisonCriterion, company: ComparisonCompany): String {
    return when (criterion.id) {
        "rating" -> "${company.rating}★"
        "price" -> company.priceRange.displayName
        "experience" -> "${company.yearsInBusiness} years"
        "team" -> company.teamSize
        "response" -> company.responseTime
        "portfolio" -> "${company.portfolioCount} projects"
        "satisfaction" -> "${(company.clientSatisfaction * 100).toInt()}%"
        else -> "N/A"
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyComparisonToolPreview() {
    TripBookTheme {
        CompanyComparisonTool(
            availableCompanies = listOf(
                ComparisonCompany(
                    id = "comp_1",
                    name = "TechSolutions Inc.",
                    logoResId = 0,
                    rating = 4.8f,
                    reviewCount = 124,
                    priceRange = PriceRange.PREMIUM,
                    location = "New York, NY",
                    specialties = listOf("Web Development", "Mobile Apps"),
                    yearsInBusiness = 8,
                    teamSize = "50-100",
                    responseTime = "< 2 hours",
                    certifications = listOf("ISO 9001", "AWS Certified"),
                    portfolioCount = 150,
                    clientSatisfaction = 0.95f
                ),
                ComparisonCompany(
                    id = "comp_2",
                    name = "Digital Innovators",
                    logoResId = 0,
                    rating = 4.6f,
                    reviewCount = 89,
                    priceRange = PriceRange.MODERATE,
                    location = "San Francisco, CA",
                    specialties = listOf("UI/UX Design", "Branding"),
                    yearsInBusiness = 5,
                    teamSize = "10-25",
                    responseTime = "< 4 hours",
                    certifications = listOf("Google Partner"),
                    portfolioCount = 75,
                    clientSatisfaction = 0.92f
                )
            ),
            selectedCompanies = listOf("comp_1", "comp_2")
        )
    }
}
