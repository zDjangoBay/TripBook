package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.android.tripbook.data.User

@Composable
fun UserProfileEmbedding(
    user: User,
    modifier: Modifier = Modifier,
    size: ProfileSize = ProfileSize.Medium
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Profile Picture (circular with initials)
        Box(
            modifier = Modifier
                .size(size.imageSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getInitials(user.name),
                fontSize = (size.imageSize.value * 0.4).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // User Info
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // User Name
            Text(
                text = user.name,
                fontSize = size.nameTextSize,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Destination with location icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(size.iconSize)
                )
                Text(
                    text = user.destination,
                    fontSize = size.destinationTextSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun getInitials(name: String): String {
    return name.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .take(2)
        .joinToString("")
}

enum class ProfileSize(
    val imageSize: Dp,
    val nameTextSize: TextUnit,
    val destinationTextSize: TextUnit,
    val iconSize: Dp
) {
    Small(28.dp, 14.sp, 12.sp, 12.dp),
    Medium(40.dp, 16.sp, 14.sp, 14.dp),
    Large(48.dp, 18.sp, 16.sp, 16.dp)
}
