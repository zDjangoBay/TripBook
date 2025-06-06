package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.* // Pour MaterialTheme, Card, Column, Text, etc.
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Pour painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview // Pour @Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Pour AsyncImage

import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.data.model.TravelLocation // Import canonique pour TravelLocation
import com.android.tripbook.data.model.Comment // Import canonique pour Comment
import com.android.tripbook.posts.model.PostVisibility // Import pour PostVisibility

import com.android.tripbook.ui.theme.TripBookTheme // Import pour le thème de la Preview

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.net.Uri // Pour ImageModel Uri




@Composable
fun PostCard(
    post: PostModel,
    onCardClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentUserId: String = "current_user"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.userAvatar, // Utilise l'avatar de l'utilisateur
                    contentDescription = "${post.username}'s Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                    // Utilise R.drawable de notre projet TripBook
                    error = painterResource(id = com.android.tripbook.R.drawable.ic_avatar_placeholder),
                    placeholder = painterResource(id = com.android.tripbook.R.drawable.ic_avatar_placeholder)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.username,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (post.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified User",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = post.createdAt.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM dd, hh:mma", Locale.ENGLISH)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (post.location.name.isNotBlank() || post.location.description?.isNotBlank() == true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        // Affiche le nom et la description de TravelLocation
                        text = "${post.location.name}" + (post.location.description?.let { ", $it" } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (post.images.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(post.images.take(3), key = { it.id }) { image ->
                        AsyncImage(
                            model = image.uri,
                            contentDescription = "Post Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                    if (post.images.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${post.images.size - 3}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) // Renommé Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onLikeClick)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (post.likes.contains(currentUserId)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.likes.contains(currentUserId)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${post.likes.size} likes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onCommentClick)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${post.comments.size} comments",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewPostCard() {
    TripBookTheme {
        val samplePost = PostModel(
            id = "p1",
            userId = "user1",
            username = "ExplorerGal",
            userAvatar = null,
            isVerified = true,
            title = "Amazing Trip to Mount Cameroon!",
            description = "Just got back from an incredible hike up Mount Cameroon. The views were breathtaking and the challenge was exhilarating! Highly recommend for adventure seekers. #hiking #cameroon #adventure",
            images = listOf(
                ImageModel("img1", Uri.parse("https://via.placeholder.com/400/A0A0A0/FFFFFF?text=Mount+Cameroon")),
                ImageModel("img2", Uri.parse("https://via.placeholder.com/400/808080/FFFFFF?text=Summit+View")),
                ImageModel("img3", Uri.parse("https://via.placeholder.com/400/606060/FFFFFF?text=Forest+Path")),
                ImageModel("img4", Uri.parse("https://via.placeholder.com/400/404040/FFFFFF?text=Volcano"))
            ),
            location = TravelLocation(
                "loc1",
                "Mount Cameroon",
                4.1667,
                9.2500,
                "Buea, Cameroon", // Description du lieu
                null
            ),
            tags = emptyList(),
            createdAt = Instant.now().minusSeconds(3600 * 24 * 3),
            lastEditedAt = null,
            visibility = PostVisibility.PUBLIC,
            collaborators = emptyList(),
            isEphemeral = false,
            ephemeralDurationMillis = null,
            likes = listOf("user_a", "user_b", "current_user"),
            comments = listOf(
                Comment("c1", "p1", "user_c", "Commenter1", null, "Looks amazing!", Instant.now().minusSeconds(100)),
                Comment("c2", "p1", "user_d", "Commenter2", null, "So jealous!", Instant.now().minusSeconds(50))
            )
        )
        PostCard(post = samplePost, onCardClick = {}, onLikeClick = {}, onCommentClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostCardNoImages() {
    TripBookTheme {
        val samplePost = PostModel(
            id = "p2",
            userId = "user2",
            username = "FoodieTraveller",
            userAvatar = null,
            isVerified = false,
            title = "Exploring the Flavors of Bangkok",
            description = "Had an incredible time trying street food in Bangkok! The Pad Thai was to die for. Definitely a must-visit for food lovers.",
            images = emptyList(),
            location = TravelLocation(
                "loc2",
                "Bangkok",
                13.7563,
                100.5018,
                "Bangkok, Thailand", // Description du lieu
                null
            ),
            tags = emptyList(),
            createdAt = Instant.now().minusSeconds(3600 * 5),
            lastEditedAt = null,
            visibility = PostVisibility.PUBLIC,
            collaborators = emptyList(),
            isEphemeral = false,
            ephemeralDurationMillis = null,
            likes = listOf("user_e"),
            comments = emptyList()
        )
        PostCard(post = samplePost, onCardClick = {}, onLikeClick = {}, onCommentClick = {})
    }
}
