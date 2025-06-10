package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryItem(
    val name: String,
    val description: String,
    val color: Color
)

val mockCategories = listOf(
    CategoryItem(
        "Technology",
        "Innovations and companies in software, hardware, and IT services.",
        Color(0xFF81D4FA) // Light Blue
    ),
    CategoryItem(
        "Health",
        "Healthcare providers, medical equipment, and wellness services.",
        Color(0xFFA5D6A7) // Light Green
    ),
    CategoryItem(
        "Finance",
        "Banks, investment firms, and financial service companies.",
        Color(0xFFFFF59D) // Light Yellow
    ),
    CategoryItem(
        "Education",
        "Schools, online courses, and educational tools and platforms.",
        Color(0xFFFFCC80) // Light Orange
    ),
    CategoryItem(
        "Retail",
        "Stores, e-commerce platforms, and consumer goods sellers.",
        Color(0xFFCE93D8) // Light Purple
    ),
    CategoryItem(
        "Travel",
        "Agencies, airlines, hotels, and tourism-related businesses.",
        Color(0xFF90CAF9) // Medium Blue
    ),
    CategoryItem(
        "Food & Beverage",
        "Restaurants, food products, and beverage manufacturers.",
        Color(0xFFFFAB91) // Light Red-Orange
    ),
    CategoryItem(
        "Entertainment",
        "Media, gaming, movies, and performing arts companies.",
        Color(0xFFB39DDB) // Lavender
    ),
    CategoryItem(
        "Real Estate",
        "Property developers, agents, and real estate services.",
        Color(0xFF80CBC4) // Teal
    ),
    CategoryItem(
        "Automotive",
        "Manufacturers, dealerships, and automotive services.",
        Color(0xFFB0BEC5) // Grayish Blue
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Browse companies by category",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val rows = mockCategories.chunked(2)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { category ->
                        CategoryCard(
                            category = category,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.3f),  // taller cards to fit description
                            onClick = {
                                // Handle category click (navigate or filter)
                            }
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = category.color)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.name,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
