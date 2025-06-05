package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.posts.model.ImageModel // Use ImageModel
import com.android.tripbook.ui.theme.TripBookTheme
import android.net.Uri

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostMediaViewer(images: List<ImageModel>) { // Changed to take List<ImageModel>
    if (images.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { images.size })

    HorizontalPager(state = pagerState) { page ->
        AsyncImage(
            model = images[page].uri, // Access URI from ImageModel
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostMediaViewer() {
    TripBookTheme {
        val sampleImages = listOf(
            ImageModel("img1", Uri.parse("https://via.placeholder.com/300/FF5733/FFFFFF?text=Image+1")),
            ImageModel("img2", Uri.parse("https://via.placeholder.com/300/33FF57/FFFFFF?text=Image+2")),
            ImageModel("img3", Uri.parse("https://via.placeholder.com/300/3357FF/FFFFFF?text=Image+3"))
        )
        PostMediaViewer(images = sampleImages)
    }
}