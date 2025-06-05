/*
This composable presents an interactive and well-organized company profile
 */
package com.android.tripbook.companycatalog.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.model.Company
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.android.tripbook.ui.theme.Purple700
import com.android.tripbook.companycatalog.model.CompanyRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    company: Company, // This company object is now from the NavGraph, which might be a copy
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Fetch the actual company object from the repository to ensure it's observable
    // This is crucial for the like count to update across screens.
    val liveCompany = CompanyRepository.getCompanyById(company.id) ?: company

    // State for the like button. Its value will now reflect the liveCompany's likes.
    // We'll assume a company is 'liked' if its like count is odd, or you can add a separate 'isLiked' boolean to Company.
    // For simplicity, let's just use the `likes` count to determine the tint.
    // If you need a proper `isLiked` state, add `var isLiked: Boolean` to your `Company` data class.
    // For this example, I'll add `var isLiked: Boolean` to Company model for clarity.

    // Let's assume `Company` now has `var isLiked: Boolean`
    var isLikedState by remember { mutableStateOf(liveCompany.isLiked) }
    var currentLikesState by remember { mutableStateOf(liveCompany.likes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = liveCompany.name, // Use liveCompany for title
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Toggle the like status in the repository
                        CompanyRepository.toggleLike(liveCompany.id, !isLikedState)
                        // Update local state to reflect the change immediately
                        isLikedState = !isLikedState
                        currentLikesState = if (isLikedState) currentLikesState + 1 else currentLikesState - 1
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            tint = if (isLikedState) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple700
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(liveCompany.imageResIds) { imageResId -> // Use liveCompany for images
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Image of ${liveCompany.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Likes",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$currentLikesState", // Display currentLikesState
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(liveCompany.stars) { // Use liveCompany for stars
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CompanyDescription(description = liveCompany.description) // Use liveCompany for description
                Spacer(modifier = Modifier.height(16.dp))

                CompanyServices(services = liveCompany.services) // Use liveCompany for services
                Spacer(modifier = Modifier.height(16.dp))

                ContactUs(contacts = liveCompany.contacts) // Use liveCompany for contacts
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
