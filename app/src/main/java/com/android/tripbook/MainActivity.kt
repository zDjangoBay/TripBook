
package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.companycatalog.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripBookApp()
            }
        }
    }
}

data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBookApp() {
    var currentScreen by remember { mutableStateOf("home") }

    val features = listOf(
        FeatureItem("Safety Tips", Icons.Default.Warning, "Stay safe while traveling"),
        FeatureItem("Travel Stories", Icons.Default.Article, "Read amazing travel experiences"),
        FeatureItem("Photo Gallery", Icons.Default.PhotoLibrary, "Discover beautiful destinations"),
        FeatureItem("Traveler Network", Icons.Default.People, "Connect with fellow travelers"),
        FeatureItem("Hidden Gems", Icons.Default.Explore, "Discover secret locations")
    )

    Scaffold(
        topBar = {
            if (currentScreen != "home") {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                "safety" -> "Safety Tips"
                                "stories" -> "Travel Stories"
                                "photos" -> "Photo Gallery"
                                "network" -> "Traveler Network"
                                "gems" -> "Hidden Gems"
                                else -> "TripBook"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { currentScreen = "home" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    features = features,
                    onFeatureClick = { feature ->
                        currentScreen = when (feature.title) {
                            "Safety Tips" -> "safety"
                            "Travel Stories" -> "stories"
                            "Photo Gallery" -> "photos"
                            "Traveler Network" -> "network"
                            "Hidden Gems" -> "gems"
                            else -> "home"
                        }
                    }
                )
                "safety" -> SafetyScreen()
                "stories" -> StoriesScreen()
                "photos" -> PhotoGalleryScreen()
                "network" -> TravelerNetworkScreen()
                "gems" -> HiddenGemsScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(
    features: List<FeatureItem>,
    onFeatureClick: (FeatureItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌍",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "Welcome to TripBook",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Your travel companion for exploring Africa & beyond",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Text(
            text = "Explore Features",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(features) { feature ->
                FeatureCard(
                    feature = feature,
                    onClick = { onFeatureClick(feature) }
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun TripBookAppPreview() {
    TripBookTheme {
        TripBookApp()
    }
}
