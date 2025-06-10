package com.android.tripbook.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGallery(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return
    
    var showFullscreenGallery by remember { mutableStateOf(false) }
    var initialPage by remember { mutableStateOf(0) }
    
    Box(modifier = modifier.height(200.dp)) {
        val pagerState = rememberPagerState { images.size }
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = "Trip image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        initialPage = page
                        showFullscreenGallery = true
                    }
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${images.size} photos",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
        
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) 
                                Color.White 
                            else 
                                Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
    
    if (showFullscreenGallery) {
        FullscreenGalleryDialog(
            images = images,
            initialPage = initialPage,
            onDismiss = { showFullscreenGallery = false }
        )
    }
}
