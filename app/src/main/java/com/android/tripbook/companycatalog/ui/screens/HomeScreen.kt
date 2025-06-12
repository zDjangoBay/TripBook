@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { DynamicGreetingSection() }
        item { QuickNavigationTiles(navController) }
        item { FeaturedCompanySection() }
        item { CategoriesPreview() }
        item { WhatsNewSection() }
        item { TrendingTagsSection() }
        item { QuoteOfTheDaySection() }
        item { LastVisitedCompanySection(context) }
        item { BookmarkedCompaniesSection() }
        item { SettingsButtonSection(navController) }

    }
}

@Composable
fun DynamicGreetingSection() {
    val greeting = when (LocalTime.now().hour) {
        in 0..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    Text(
        text = "$greeting, Welcome to Company Catalog Home",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun QuickNavigationTiles(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Explore", "Categories", "Stories", "Tips", "Safety").forEach {
                AssistChip(
                    onClick = {
                        when (it) {
                            "Categories" -> navController.navigate("categories")
                            "Stories" -> navController.navigate("stories")
                            "Tips" -> navController.navigate("tips")
                            "Safety" -> navController.navigate("safety")
                            // TODO: Add explore route if needed
                        }
                    },
                    label = { Text(it) },
                    shape = MaterialTheme.shapes.medium,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        
        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Hidden Gems", "Photo Gallery", "Traveler Network").forEach {
                AssistChip(
                    onClick = {
                        when (it) {
                            "Hidden Gems" -> navController.navigate("hidden_gems")
                            "Photo Gallery" -> navController.navigate("photo_gallery")
                            "Traveler Network" -> navController.navigate("traveler_network")
                        }
                    },
                    label = { Text(it) },
                    shape = MaterialTheme.shapes.medium,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
    }
}


@Composable
fun FeaturedCompanySection() {
    val companies = MockCompanyData.companies
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentIndex = (currentIndex + 1) % companies.size
        }
    }

    val currentCompany = companies[currentIndex]

    Card(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Logo displayed here
            Image(
                painter = painterResource(id = currentCompany.logoResId),
                contentDescription = "${currentCompany.name} logo",
                modifier = Modifier
                    .size(110.dp)
                    .padding(end = 20.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quick Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "• 150+ Travel Stories")
                Text(text = "• 500+ Safety Tips")
                Text(text = "• 1000+ Photos")
            }
        }
    }
}