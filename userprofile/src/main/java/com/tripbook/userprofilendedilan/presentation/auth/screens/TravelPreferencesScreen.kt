package com.tripbook.userprofilendedilan.presentation.auth.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TravelPreferencesPage(
    preferences: List<String>,
    onPreferencesUpdated: (List<String>) -> Unit,
    destinations: List<String>,
    onDestinationsUpdated: (List<String>) -> Unit
) {
    val preferenceCategories = mapOf(
        "Nature & Outdoors" to listOf("Adventure", "Beach", "Hiking", "Wildlife", "Nature"),
        "Culture & Experience" to listOf("Cultural", "History", "Food", "Photography"),
        "Travel Styles" to listOf("Luxury", "Road Trip", "City", "Romance", "Solo")
    )
    
    // Map of destinations with their regions and colors
    val destinationInfo = mapOf(
        "Cairo" to Pair("Africa", Color(0xFFFF9800)), // Orange
        "Cameroun" to Pair("Africa", Color(0xFFB9EC0B)),
        "Mali" to Pair("Africa", Color(0xFFE51A1A)),
        "South Africa" to Pair("Africa", Color(0xFF34BD09)), // Orange
        "Ethiopie" to Pair("Africa", Color(0xFF219FE5)),
        "Tokyo" to Pair("Asia", Color(0xFF3F51B5)), // Indigo
        "Paris" to Pair("Europe", Color(0xFF2196F3)), // Blue
        "New York" to Pair("North America", Color(0xFF009688)), // Teal
        "Rome" to Pair("Europe", Color(0xFF4CAF50)), // Green
        "Bali" to Pair("Asia", Color(0xFF8BC34A)), // Light Green

        "Rio" to Pair("South America", Color(0xFFF44336)), // Red
        "Sydney" to Pair("Oceania", Color(0xFF9C27B0))  // Purple
    )

    // Group destinations by region
    val destinationsByRegion = destinationInfo.entries.groupBy { it.value.first }

    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Travel Styles", "Dream Destinations")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Personalize Your Experience",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Select what interests you most",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                
                // Selection summary
                if (preferences.isNotEmpty() || destinations.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${preferences.size} styles · ${destinations.size} destinations",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Tab selector
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title) }
                )
            }
        }
        
        // Content based on selected tab
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with 
                fadeOut(animationSpec = tween(300))
            }
        ) { tabIndex ->
            when (tabIndex) {
                0 -> TravelStylesContent(
                    preferences = preferences,
                    onPreferencesUpdated = onPreferencesUpdated,
                    preferenceCategories = preferenceCategories
                )
                1 -> DreamDestinationsContent(
                    destinations = destinations,
                    onDestinationsUpdated = onDestinationsUpdated,
                    destinationsByRegion = destinationsByRegion
                )
            }
        }
        
        // Info card at bottom
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your preferences help us recommend better experiences",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun TravelStylesContent(
    preferences: List<String>,
    onPreferencesUpdated: (List<String>) -> Unit,
    preferenceCategories: Map<String, List<String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        preferenceCategories.forEach { (category, prefsInCategory) ->
            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            // Using Flow layout with FlexBox-like behavior
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    var currentRowItems = mutableListOf<@Composable () -> Unit>()
                    var currentRowWidth = 0f
                    val maxWidth = 1000f // Approximate screen width
                    val spacing = 8.dp
                    
                    prefsInCategory.forEach { preference ->
                        val isSelected = preferences.contains(preference)
                        
                        val chipContent: @Composable () -> Unit = {
                            SimplePreferenceChip(
                                text = preference,
                                isSelected = isSelected,
                                onClick = {
                                    val updatedPreferences = if (isSelected) {
                                        preferences - preference
                                    } else {
                                        preferences + preference
                                    }
                                    onPreferencesUpdated(updatedPreferences)
                                }
                            )
                        }
                        
                        currentRowItems.add(chipContent)
                        
                        // Simple row handling logic
                        if (currentRowItems.size >= 3) { // Rough estimate to create rows
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacing),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                currentRowItems.forEach { it() }
                            }
                            Spacer(modifier = Modifier.height(spacing))
                            currentRowItems = mutableListOf()
                        }
                    }
                    
                    // Handle remaining items
                    if (currentRowItems.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            currentRowItems.forEach { it() }
                        }
                    }
                }
            }
            
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun DreamDestinationsContent(
    destinations: List<String>,
    onDestinationsUpdated: (List<String>) -> Unit,
    destinationsByRegion: Map<String, List<Map.Entry<String, Pair<String, Color>>>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        destinationsByRegion.forEach { (region, destinationsInRegion) ->
            Text(
                text = region,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(destinationsInRegion) { (destination, info) ->
                    val isSelected = destinations.contains(destination)
                    val (_, color) = info
                    
                    SimpleDestinationCard(
                        name = destination,
                        color = color,
                        isSelected = isSelected,
                        onClick = {
                            val updatedDestinations = if (isSelected) {
                                destinations - destination
                            } else {
                                destinations + destination
                            }
                            onDestinationsUpdated(updatedDestinations)
                        }
                    )
                }
            }
            
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePreferenceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text = text) },
        leadingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        modifier = Modifier.animateContentSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDestinationCard(
    name: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.7f)
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
