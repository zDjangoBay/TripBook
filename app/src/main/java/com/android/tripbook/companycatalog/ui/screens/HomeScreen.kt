package com.android.tripbook.companycatalog.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.android.tripbook.companycatalog.MainAppScreen



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
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
        item { SettingsButtonSection(navController) }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
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

@Composable
fun WhatsNewSection() {
    val newCompanies = listOf("GreenCorp", "HealthPro", "EcoLogix")
    Column {
        Text(
            "🆕 What's New",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        newCompanies.forEach {
            Text(
                "• $it",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TrendingTagsSection() {
    val tags = listOf("#TopStartup", "#WomenLed", "#RemoteFriendly")
    Column {
        Text(
            "🔥 Trending Tags",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            tags.forEach {
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuoteOfTheDaySection() {
    val quotes = listOf(
        "Innovation distinguishes between a leader and a follower.",
        "The best way to predict the future is to create it."
    )
    val todayQuote = quotes[LocalDate.now().dayOfYear % quotes.size]
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            "💡 $todayQuote",
            Modifier.padding(16.dp),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LastVisitedCompanySection(context: Context) {
    val sharedPrefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    val lastVisited = sharedPrefs.getString("lastCompany", "None")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "📌 Last Visited Company",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "🏢 $lastVisited",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BookmarkedCompaniesSection() {
    val bookmarked = listOf("TechNova", "FinServe")
    Column {
        Text(
            "⭐ Bookmarked Companies",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        bookmarked.forEach {
            Text(
                "• $it",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsButtonSection(navController: NavController) {
    Button(
        onClick = { navController.navigate(MainAppScreen.Settings.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            "⚙️ Settings & Preferences",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
