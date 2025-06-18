package com.android.tripbook.posts.ui.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun PhotoGallery(
    photos: List<Uri>,
    modifier: Modifier = Modifier
) {
    var selectedPhotoIndex by remember { mutableStateOf(-1) }
    
    if (photos.isNotEmpty()) {
        when (photos.size) {
            1 -> {
                // Une seule photo - affichage en grand
                SinglePhotoDisplay(
                    photoUri = photos[0],
                    onClick = { selectedPhotoIndex = 0 },
                    modifier = modifier
                )
            }
            in 2..4 -> {
                // 2-4 photos - grille compacte
                PhotoGrid(
                    photos = photos,
                    onPhotoClick = { index -> selectedPhotoIndex = index },
                    modifier = modifier
                )
            }
            else -> {
                // Plus de 4 photos - rangée horizontale scrollable
                PhotoCarousel(
                    photos = photos,
                    onPhotoClick = { index -> selectedPhotoIndex = index },
                    modifier = modifier
                )
            }
        }
        
        // Dialog pour afficher la photo en plein écran
        if (selectedPhotoIndex >= 0) {
            PhotoViewerDialog(
                photos = photos,
                initialIndex = selectedPhotoIndex,
                onDismiss = { selectedPhotoIndex = -1 }
            )
        }
    }
}

@Composable
private fun SinglePhotoDisplay(
    photoUri: Uri,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photoUri)
            .crossfade(true)
            .build(),
        contentDescription = "Photo du post",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    )
}

@Composable
private fun PhotoGrid(
    photos: List<Uri>,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = if (photos.size <= 2) 1 else 2
    val itemsPerRow = if (photos.size == 3) listOf(2, 1) else listOf(2, 2)
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var photoIndex = 0
        repeat(rows) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val itemsInThisRow = itemsPerRow.getOrElse(rowIndex) { 2 }
                repeat(itemsInThisRow) {
                    if (photoIndex < photos.size) {
                        val currentIndex = photoIndex
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photos[currentIndex])
                                .crossfade(true)
                                .build(),
                            contentDescription = "Photo ${currentIndex + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onPhotoClick(currentIndex) }
                        )
                        photoIndex++
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCarousel(
    photos: List<Uri>,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(photos.take(5)) { photoUri -> // Limite à 5 photos visibles
            val index = photos.indexOf(photoUri)
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Photo ${index + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPhotoClick(index) }
                )
                
                // Indicateur s'il y a plus de photos
                if (index == 4 && photos.size > 5) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${photos.size - 5}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoViewerDialog(
    photos: List<Uri>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { photos.size }
    )
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photos[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Photo ${page + 1}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Bouton de fermeture
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = Color.White
                )
            }
            
            // Indicateur de page si plusieurs photos
            if (photos.size > 1) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${photos.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

