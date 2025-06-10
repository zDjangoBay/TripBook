package com.android.tripbook.ui.uis

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Destination
import com.android.tripbook.viewmodel.AgencyViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun AgencyDetailScreen(
    agency: Agency,
    agencyViewModel: AgencyViewModel,
    onBackClick: () -> Unit
) {
    val destinations by agencyViewModel.destinations.collectAsState()
    val isLoading by agencyViewModel.isLoading.collectAsState()
    val error by agencyViewModel.error.collectAsState()
    val context = LocalContext.current

    // Log the agency ID for debugging//
    LaunchedEffect(Unit) {
        Log.d("AgencyDetailScreen", "Agency ID: ${agency.agencyId}")
    }

    // Load destinations for the agency
    LaunchedEffect(agency.agencyId) {
        agencyViewModel.loadDestinationsForAgency(agency.agencyId)
    }

    // Log destinations state for debugging
    LaunchedEffect(destinations) {
        Log.d("AgencyDetailScreen", "Destinations state updated: $destinations")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = agency.agencyName,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                // Error Message
                error?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = { agencyViewModel.loadDestinationsForAgency(agency.agencyId) },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Retry")
                    }
                }

                // Agency Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Agency Description
                        agency.agencyDescription?.let { description ->
                            Text(
                                text = "Description",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A202C)
                            )
                            Text(
                                text = description,
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Address
                        agency.agencyAddress?.let { address ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📍",
                                    fontSize = 14.sp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = address,
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Phone
                        agency.contactPhone?.let { phone ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$phone")
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                Text(
                                    text = "📞",
                                    fontSize = 14.sp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = phone,
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Website
                        agency.website?.let { website ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val url = if (!website.startsWith("http://") && !website.startsWith("https://")) {
                                        "https://$website"
                                    } else {
                                        website
                                    }
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            ) {
                                Text(
                                    text = "🌐",
                                    fontSize = 14.sp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = website,
                                    fontSize = 14.sp,
                                    color = Color(0xFF667EEA)
                                )
                            }
                        }
                    }
                }

                // Destinations List
                Text(
                    text = "Available Destinations",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (destinations.isEmpty() && !isLoading && error == null) {
                    Text(
                        text = "No destinations available for this agency",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(destinations) { destination ->
                            DestinationCard(destination = destination)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationCard(destination: Destination) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = destination.destinationName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A202C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rating: ${destination.agencyRating}/5",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.PRC).format(destination.destinationTarif),
                    fontSize = 14.sp,
                    color = Color(0xFF667EEA)
                )
            }
        }
    }
}