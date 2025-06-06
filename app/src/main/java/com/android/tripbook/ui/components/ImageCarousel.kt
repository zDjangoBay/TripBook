
@file:OptIn(ExperimentalFoundationApi::class)
package com.android.tripbook.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(images: List<String>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    HorizontalPager(state = pagerState, modifier = modifier.height(250.dp)) { page ->
        AsyncImage(
            model = images[page],
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}
