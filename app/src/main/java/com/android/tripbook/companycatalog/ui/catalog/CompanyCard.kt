/*
- This composable efficiently formats company details into an interactive card that users can tap to navigate.
- It combines images, text, and icons for a well-structured UI.
 */
package com.android.tripbook.companycatalog.ui.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite // Import for hearticon
import androidx.compose.material.icons.filled.Star // Import for star icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon // Import for Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // For star and heart tinting
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight // Import for FontWeight
import androidx.compose.ui.text.style.TextOverflow // Import for TextOverflow
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.model.Company
@Composable
fun CompanyCard(company: Company, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp), // Card's overall rounded shape
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth() // Column takes full width
        ) { // Image at the top, spanning full width of the card's content area
            Image( // Display only the first image from the list
                painter = painterResource(id =
                    company.imageResIds.firstOrNull() ?: 0),
                contentDescription = "${company.name} image",
                contentScale = ContentScale.Crop, // Crop to fill bounds
                modifier = Modifier
                    .height(180.dp) // Increased height for the image
                    .fillMaxWidth() // Clip only top corners to match the card's top corners
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd =
                        16.dp))
            ) // Content below the image, with its own padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Padding applied to the text content area
                        horizontalAlignment = Alignment.Start // Align text to the start (left)
            ) { // Company Name (bold, no prefix)
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold // Make company name bold
                )
                Spacer(modifier = Modifier.height(4.dp)) // Description (single line with ellipsis)
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1, // Limit to one line
                    overflow = TextOverflow.Ellipsis // Add ellipsis if it overflows
                )
                Spacer(modifier = Modifier.height(8.dp)) // Likes and Stars Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Space out likes and stars
                ) { // Likes (Heart Icon + Number)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Likes",
                            tint = Color.Red, // Red heart icon
                            modifier = Modifier.size(18.dp) // Icon size
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${company.likes}", // Just the number of likes
                                    style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    } // Stars
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(company.stars) { // Display stars based on
                            company.stars
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star",
                                tint = Color(0xFFFFD700), // Gold color for  stars
                                        modifier = Modifier.size(16.dp) // Smaller star icon
                            )
                        }
                    }
                }
            }
        }
    }
}