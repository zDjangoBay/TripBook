package com.android.tripbook.ui.theme

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding



// In ui/theme/Extensions.kt
@Composable
fun Chip(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color()
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Purple40)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        label()
    }
}