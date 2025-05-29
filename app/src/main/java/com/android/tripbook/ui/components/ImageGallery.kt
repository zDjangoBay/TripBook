package com.android.tripbook.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

/**
 * A thumbnail gallery that shows images in a grid and allows opening a fullscreen gallery
 * @param images List of image URLs to display
 * @param modifier Modifier for the gallery container
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGallery(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return
    
    var showFullscreenGallery by remember { mutableStateOf(false) }
    var initialPage by remember { mutableStateOf(0) }
    
    // Thumbnail grid display
    Box(modifier = modifier.height(200.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Main large image
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        initialPage = 0
                        showFullscreenGallery = true
                    }
            ) {
                AsyncImage(
                    model = images.firstOrNull(),
                    contentDescription = "Trip image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Image counter badge
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
            }
            
            // Side thumbnails column
            if (images.size > 1) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Show up to 2 thumbnails on the side
                    val sideImages = images.drop(1).take(2)
                    sideImages.forEachIndexed { index, imageUrl ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    initialPage = index + 1
                                    showFullscreenGallery = true
                                }
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Trip image ${index + 2}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Show "more" indicator on the last thumbnail if there are more images
                            if (index == sideImages.lastIndex && images.size > 3) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${images.size - 3}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Fullscreen gallery dialog - Using if statement instead of conditional call
    if (showFullscreenGallery) {
        FullscreenImageGallery(
            images = images,
            initialPage = initialPage,
            onDismiss = { 
                showFullscreenGallery = false 
            }
        )
    }
}

/**
 * Fullscreen image gallery with zoom and swipe capabilities
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FullscreenImageGallery(
    images: List<String>,
    initialPage: Int,
    onDismiss: () -> Unit
) {
    // Using Dialog with fullscreen properties
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            val pagerState = rememberPagerState(initialPage = initialPage) { images.size }
            
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close gallery",
                    tint = Color.White
                )
            }
            
            // Image pager with zoom
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                ZoomableImage(
                    imageUrl = images[page],
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Page indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${images.size}",
                    color = Color.White
                )
            }
        }
    }
}

/**
 * A zoomable image component that supports pinch-to-zoom and panning
 */
@Composable
private fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    // Add double-tap detection to reset zoom
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 3f)
                    
                    // Only allow panning when zoomed in
                    if (scale > 1f) {
                        val maxX = (size.width * (scale - 1)) / 2
                        val maxY = (size.height * (scale - 1)) / 2
                        
                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Zoomable image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
    
    // Reset zoom when image changes
    LaunchedEffect(imageUrl) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }
}
