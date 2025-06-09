package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class CategoryItem(
    val name: String
)

val mockCategories = listOf(
    CategoryItem("Technology"),
    CategoryItem("Health"),
    CategoryItem("Finance"),
    CategoryItem("Education"),
    CategoryItem("Retail"),
    CategoryItem("Travel"),
    CategoryItem("Food & Beverage"),
    CategoryItem("Entertainment"),
    CategoryItem("Real Estate"),
    CategoryItem("Automotive")
)

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CategoriesScreen(navController: NavController) {
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

            // We'll manually create a grid with 2 columns using a simple loop
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
                                .aspectRatio(1f),
                            onClick = {
                                // Handle category click, e.g. navigate or filter
                            }
                        )
                    }
                    // If row has only 1 item, add spacer to fill space
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = category.name,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
