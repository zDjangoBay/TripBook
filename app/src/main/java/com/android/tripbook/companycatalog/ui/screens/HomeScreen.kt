package com.android.tripbook.companycatalog.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.companycatalog.data.MockCompanyData
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { DynamicGreetingSection() }
        item { QuickNavigationTiles() }
        item { FeaturedCompanySection() }
        item { CategoriesPreview() }
        item { WhatsNewSection() }
        item { TrendingTagsSection() }
        item { QuoteOfTheDaySection() }
        item { LastVisitedCompanySection(context) }
        item { BookmarkedCompaniesSection() }
        item { SettingsButtonSection() }
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
fun QuickNavigationTiles() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf("Explore", "Categories", "Saved", "News", "Feedback").forEach {
            AssistChip(
                onClick = { /* Navigate */ },
                label = { Text(it) },
                shape = MaterialTheme.shapes.medium,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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
            // Uncomment and use if you have logos:
            /*
            Image(
                painter = painterResource(id = currentCompany.logoResId),
                contentDescription = "${currentCompany.name} logo",
                modifier = Modifier
                    .size(110.dp)
                    .padding(end = 20.dp)
            )
            */

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "🌟 Featured Company",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentCompany.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentCompany.description,
                    maxLines = 4,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { /* Learn more action */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Learn More", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun CategoriesPreview() {
    Column {
        Text(
            "🏷️ Categories",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            listOf("Tech", "Health", "Finance").forEach {
                AssistChip(
                    onClick = { /* Filter */ },
                    label = { Text(it) },
                    modifier = Modifier.padding(end = 10.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}


