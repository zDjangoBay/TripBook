
package com.android.tripbook.companycatalog.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.companycatalog.data.Company
import com.android.tripbook.companycatalog.data.MockData
import com.android.tripbook.companycatalog.ui.components.SectionTitle

@Composable
fun CompanyDetailScreen(
    companyId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val company = remember(companyId) {
        MockData.companies.find { it.id == companyId }
    }
    
    if (company == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Company not found",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    var isFavorite by remember { mutableStateOf(company.isFavorite) }
    var isFollowing by remember { mutableStateOf(company.isFollowing) }
    
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            // Header Image
            Box(modifier = Modifier.height(250.dp)) {
                AsyncImage(
                    model = company.imageUrl,
                    contentDescription = company.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isFavorite = !isFavorite }
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    
                    IconButton(onClick = { /* Handle share */ }) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        item {
            // Company Info
            Column(modifier = Modifier.padding(16.dp)) {
                // Title and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = company.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (company.isVerified) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = company.category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Follow Button
                    OutlinedButton(
                        onClick = { isFollowing = !isFollowing }
                    ) {
                        Icon(
                            if (isFollowing) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isFollowing) "Following" else "Follow")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rating and Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(Icons.Default.Star, "${company.rating}", "Rating")
                    StatItem(Icons.Default.ThumbUp, "${company.likes}", "Likes")
                    StatItem(Icons.Default.RemoveRedEye, "${company.views}", "Views")
                    StatItem(Icons.Default.People, "${company.followers}", "Followers")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
        
        item {
            // Contact Information
            SectionTitle(title = "Contact Information")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ContactItem(Icons.Default.LocationOn, "${company.city}, ${company.region}")
                    ContactItem(Icons.Default.Phone, company.phoneNumber)
                    ContactItem(Icons.Default.Email, company.email)
                    if (company.website.isNotEmpty()) {
                        ContactItem(Icons.Default.Language, company.website)
                    }
                    ContactItem(Icons.Default.Schedule, company.openingHours)
                }
            }
        }
        
        item {
            // Services
            CompanyServices(
                services = company.services,
                amenities = company.amenities
            )
        }
        
        item {
            // Image Gallery
            if (company.imageUrls.size > 1) {
                SectionTitle(title = "Gallery")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(company.imageUrls) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
        
        item {
            // Special Offers
            if (company.specialOffers.isNotEmpty()) {
                SectionTitle(title = "Special Offers")
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    company.specialOffers.forEach { offer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = offer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
