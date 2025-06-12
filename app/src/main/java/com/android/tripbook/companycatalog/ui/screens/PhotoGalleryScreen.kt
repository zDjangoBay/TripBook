@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class PhotoItem(
    val id: Int,
    val title: String,
    val location: String
)

@Composable
fun PhotoGalleryScreen() {
    val photos = listOf(
        PhotoItem(1, "Mountain View", "Swiss Alps"),
        PhotoItem(2, "Beach Sunset", "Maldives"),
        PhotoItem(3, "City Lights", "Tokyo"),
        PhotoItem(4, "Wildlife", "Kenya Safari"),
        PhotoItem(5, "Forest Trail", "Amazon"),
        PhotoItem(6, "Desert Dunes", "Sahara")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photo ->
            PhotoCard(photo = photo)
        }
    }
}

@Composable
fun PhotoCard(photo: PhotoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = photo.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class PhotoItem(
    val id: String,
    val title: String,
    val location: String,
    val photographer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen() {
    val photos = listOf(
        PhotoItem("1", "Sunset at Limbe Beach", "Limbe, Cameroon", "Travel Explorer"),
        PhotoItem("2", "Mount Cameroon Trek", "Buea, Cameroon", "Adventure Seeker"),
        PhotoItem("3", "Douala City Lights", "Douala, Cameroon", "City Photographer"),
        PhotoItem("4", "Waza National Park", "Far North, Cameroon", "Wildlife Expert"),
        PhotoItem("5", "Kribi Beaches", "Kribi, Cameroon", "Beach Lover"),
        PhotoItem("6", "Yaoundé Cathedral", "Yaoundé, Cameroon", "Architecture Fan")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Gallery") },
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
                text = "Discover beautiful places through photos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(photos) { photo ->
                    PhotoCard(photo = photo)
                }
            }
        }
    }
}

@Composable
fun PhotoCard(photo: PhotoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📸",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = photo.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = photo.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "by ${photo.photographer}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
