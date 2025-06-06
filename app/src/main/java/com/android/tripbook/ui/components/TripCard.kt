
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
import com.android.tripbook.model.Trip



@Composable
fun TripCard(
    trip: Trip, // <<<< THIS MUST BE YOUR UNIFIED TRIP MODEL
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    miniProfileContent: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            val imageUrl = trip.imageUrl.firstOrNull() // Uses the first image from the list

            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl,

                ),
                contentDescription = trip.title, // Good for accessibility
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(Modifier.padding(12.dp)) {
                miniProfileContent?.invoke()

                if (miniProfileContent != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trip.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))


            }
        }
    }
}