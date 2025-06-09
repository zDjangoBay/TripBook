package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Company(
    val id: Int,
    val name: String,
    val description: String,
    val rating: Int,
    val likes: Int
)

@Composable
fun ExploreScreen() {
    val companies = remember {
        listOf(
            Company(1, "SkyJet Travel", "Best flight and hotel deals.", 5, 1250),
            Company(2, "Urban Tours", "Local guides and unique experiences.", 4, 980),
            Company(3, "EcoExplorer", "Eco-friendly travel and transport.", 4, 540),
            Company(4, "LuxuryEscape", "Luxury destinations and premium hotels.", 5, 1600),
            Company(5, "Adventure Co.", "Outdoor and adventure packages.", 4, 430),
            Company(6, "TripBuilder", "Plan and customize your trip easily.", 4, 690)
        )
    }

    Surface(modifier = Modifier.fillMaxSize().background(Color(0xFFE8F5E9))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Explore the world with TripBook!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1B5E20)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Find your next adventure among top-rated companies.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF388E3C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(companies) { company ->
                    CompanyCard(company)
                }
            }
        }
    }
}

@Composable
fun CompanyCard(company: Company) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate or show details */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = company.name, fontSize = 20.sp, color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = company.description, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "⭐ Rating: ${company.rating}    ❤️ Likes: ${company.likes}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenPreview() {
    ExploreScreen()
}
