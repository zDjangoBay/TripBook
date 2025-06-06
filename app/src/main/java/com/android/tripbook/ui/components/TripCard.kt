package com.android.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.Model.Trip
import com.android.tripbook.Model.User

/**
 * Renders a single trip card with image, title, and description.
 * @param trip The Trip object containing the trip data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    trip: Trip,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    miniProfileContent: @Composable (() -> Unit)? = null
) {
    // For demo: create a mock list of users for each trip (replace with real user data in production)
    val users = listOf(
        User("alice", "https://randomuser.me/api/portraits/women/1.jpg", "Alice"),
        User("bob", "https://randomuser.me/api/portraits/men/2.jpg", "Bob"),
        User("carol", "https://randomuser.me/api/portraits/women/3.jpg", "Carol"),
        User("dan", "https://randomuser.me/api/portraits/men/4.jpg", "Dan"),
        User("eve", "https://randomuser.me/api/portraits/women/5.jpg", "Eve")
    )
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Use the first image URL if available, else fallback to empty string
            val imageUrl = trip.imageUrl.firstOrNull().orEmpty()

            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            miniProfileContent?.invoke()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = trip.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = trip.caption,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
