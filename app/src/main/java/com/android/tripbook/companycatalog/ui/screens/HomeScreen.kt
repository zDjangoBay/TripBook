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


