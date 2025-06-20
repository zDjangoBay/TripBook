package com.android.tripbook.ui.components.comment_libby.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.ui.components.comment_libby.CommentPopup

/**
 * Example of how to use CommentPopup in your existing screens
 * Created by Libby
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreenExample() {
    var showCommentsForTrip by remember { mutableStateOf<String?>(null) }

    // Sample trip data
    val trips = remember {
        listOf(
            Trip("trip_001", "Safari Adventure", "Kenya", "5 days amazing safari experience"),
            Trip("trip_002", "Beach Paradise", "Zanzibar", "Relaxing beach vacation"),
            Trip("trip_003", "Mountain Hiking", "Kilimanjaro", "Epic mountain climbing adventure"),
            Trip("trip_004", "Cultural Tour", "Morocco", "Explore rich Moroccan culture")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Trip Catalog",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trips) { trip ->
                TripCard(
                    trip = trip,
                    onCommentClick = { tripId ->
                        showCommentsForTrip = tripId
                    }
                )
            }
        }
    }

    // Comment Popup - This is how you integrate it
    showCommentsForTrip?.let { tripId ->
        CommentPopup(
            isVisible = true,
            tripId = tripId,
            onDismiss = { showCommentsForTrip = null },
            onCommentSubmit = { comment ->
                // Handle comment submission
                println("Comment submitted for trip $tripId: $comment")
                // You can add your API call here to save the comment
            }
        )
    }
}

@Composable
private fun TripCard(
    trip: Trip,
    onCommentClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = trip.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = trip.location,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = trip.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onCommentClick(trip.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "View Comments",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class Trip(
    val id: String,
    val title: String,
    val location: String,
    val description: String
)
