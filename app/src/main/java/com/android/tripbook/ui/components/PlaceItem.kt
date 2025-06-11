package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.model.Place





@Composable
fun PlaceItem(place: Place, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(215.dp)
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
            .background(Color.Transparent)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = place.picUrl,
            contentDescription = place.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )

        // Semi-transparent black overlay (like half_black_bg)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x80000000)) // 50% black
        )

        // Bottom text with half black background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0x80000000)), // same 50% black overlay
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = place.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

