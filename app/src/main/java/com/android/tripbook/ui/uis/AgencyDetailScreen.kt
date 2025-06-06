package com.android.tripbook.ui.uis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.andriod.tripbook.ui.viewmodel.AgencyDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgencyDetailScreen(
    navController: NavController,
    agencyId: String?,
    viewModel: AgencyDetailViewModel = viewModel() // ViewModel with SavedStateHandle
) {
    val agency by viewModel.agency.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(agency?.name ?: "Agency Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        if (agency == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Agency Logo/Placeholder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (agency?.logoUrl != null) {
                        AsyncImage(
                            model = agency?.logoUrl,
                            contentDescription = "${agency?.name} logo",
                            modifier = Modifier
                                .size(96.dp)
                                .padding(end = 16.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .padding(end = 16.dp)
                                .Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                agency?.name?.split(" ")?.map { it.firstOrNull()?.uppercaseChar() }?.joinToString("") ?: "",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column {
                        Text(agency?.name ?: "N/A", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700), // Gold color
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${"%.1f".format(agency?.rating ?: 0.0)} (${agency?.reviewCount} reviews)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (agency?.isVerified == true) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Agency",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(" Verified", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(agency?.description ?: "No description available.", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Services Offered", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                agency?.servicesOffered?.forEach { service ->
                    Text("• $service", style = MaterialTheme.typography.bodyMedium)
                }
                if (agency?.servicesOffered.isNullOrEmpty()) {
                    Text("No services listed.", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Contact Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                agency?.contactEmail?.let { email ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(email, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                agency?.contactPhone?.let { phone ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(phone, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (agency?.contactEmail.isNullOrBlank() && agency?.contactPhone.isNullOrBlank()) {
                    Text("No contact information available.", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button to Book
                Button(
                    onClick = {
                        // Navigate to BookingScreen, passing agencyId
                        agency?.let {
                            navController.navigate("booking_screen/${it.id}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book a Service", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}