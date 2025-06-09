package com.android.tripbook.companycatalog.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.companycatalog.data.MockCompanyData

@Composable
fun CompanyDetailScreen(companyId: String) {
    val company = MockCompanyData.companies.find { it.id == companyId }

    if (company == null) {
        Text("Company not found", modifier = Modifier.padding(16.dp))
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Company logo
                    Image(
                        painter = painterResource(id = company.logoResId),
                        contentDescription = "${company.name} Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(bottom = 12.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Company name
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = company.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Services Offered
                    Text("Services Offered:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    company.servicesOffered.forEach {
                        Text("• $it", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact Info
                    company.contactInfo?.let { contacts ->
                        Text("Contact Info:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        contacts.forEach { (type, value) ->
                            Text("$type: $value", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address
                    Text("Address:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(company.address, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ratings
                    Text("Rating:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "${company.averageRating} ⭐ (${company.numberOfReviews} reviews)",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Social Media
                    company.socialMediaLinks?.takeIf { it.isNotEmpty() }?.let { links ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Social Media:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        links.forEach { (platform, url) ->
                            Text("$platform: $url", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
