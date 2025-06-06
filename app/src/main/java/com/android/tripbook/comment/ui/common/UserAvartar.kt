package com.android.tripbook.comment.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.android.tripbook.R

@Composable
fun UserAvatar(imageUrl: String?, size: Dp = 40.dp) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "User Avatar",
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, Color.Gray, CircleShape),
        placeholder = painterResource(id = R.drawable.ic_user_placeholder),
        error = painterResource(id = R.drawable.ic_user_placeholder)
    )
}