
package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R

data class TravelPhoto(
    val id: String,
    val imageResId: Int,
    val location: String,
    val caption: String,
    val photographerName: String,
    val timeAgo: String
)

@Composable
fun PhotoGalleryScreen() {
    val photos = remember {
        listOf(
            TravelPhoto(
                id = "1",
                imageResId = R.drawable.mock_logo_1,
                location = "Serengeti National Park",
                caption = "Wildlife safari at its finest",
                photographerName = "David Wilson",
                timeAgo = "1 hour ago"
            ),
            TravelPhoto(
                id = "2",
                imageResId = R.drawable.mock_logo_2,
                location = "Zanzibar Beach",
                caption = "Crystal clear waters",
                photographerName = "Lisa Brown",
                timeAgo = "3 hours ago"
            ),
            TravelPhoto(
                id = "3",
                imageResId = R.drawable.mock_logo_3,
                location = "Ethiopian Highlands",
                caption = "Mountain trekking adventure",
                photographerName = "Ahmed Hassan",
                timeAgo = "1 day ago"
            ),
            TravelPhoto(
                id = "4",
                imageResId = R.drawable.mock_logo_4,
                location = "Sahara Desert",
                caption = "Camel trek at sunset",
                photographerName = "Maria Garcia",
                timeAgo = "2 days ago"
            ),
            TravelPhoto(
                id = "5",
                imageResId = R.drawable.mock_logo_5,
                location = "Kruger National Park",
                caption = "Big Five spotted!",
                photographerName = "John Smith",
                timeAgo = "1 week ago"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Photo Gallery",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            
            FloatingActionButton(
                onClick = { /* Open camera/gallery */ },
                modifier = Modifier.size(56.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Photo Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(photos) { photo ->
                PhotoCard(photo = photo)
            }
        }
    }
}

@Composable
fun PhotoCard(photo: TravelPhoto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            androidx.compose.foundation.Image(
                painter = painterResource(id = photo.imageResId),
                contentDescription = photo.caption,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // Photo info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = photo.location,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Text(
                    text = photo.caption,
                    color = Color.White,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                
                Text(
                    text = "by ${photo.photographerName}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 8.sp
                )
            }
        }
    }
}
