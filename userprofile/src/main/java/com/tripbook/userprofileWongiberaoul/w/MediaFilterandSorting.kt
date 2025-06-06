package com.tripbook.userprofileWongiberaoul.w


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaFilter

@Composable
fun FilterTabs(
    currentFilter: MediaFilter,
    onFilterChange: (MediaFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterTab(
            text = "All Media",
            isSelected = currentFilter == MediaFilter.ALL,
            onClick = { onFilterChange(MediaFilter.ALL) }
        )
        FilterTab(
            text = "Photos",
            isSelected = currentFilter == MediaFilter.PHOTOS,
            onClick = { onFilterChange(MediaFilter.PHOTOS) }
        )
        FilterTab(
            text = "Videos",
            isSelected = currentFilter == MediaFilter.VIDEOS,
            onClick = { onFilterChange(MediaFilter.VIDEOS) }
        )
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFCCC2DC) else Color.White
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (isSelected) Color.White else Color(0xFF2C3E50),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

