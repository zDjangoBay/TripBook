package com.android.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransportOption(iconResId: Int, labelResId: Int, backgroundColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp) // Slightly larger than default
    ) {
        Box(
            modifier = Modifier
                .size(64.dp) // Reasonable size for background box
                .background(color = backgroundColor, shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = stringResource(labelResId),
                modifier = Modifier.size(40.dp), // Slightly larger icon
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(id = labelResId),
            fontSize = 13.sp,
            color = Color.Black
        )
    }
}


