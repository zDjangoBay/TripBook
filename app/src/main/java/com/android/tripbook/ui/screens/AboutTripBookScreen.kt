package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.navigation.compose.composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutTripBookScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About TripBook") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), 
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "TripBook is a community-powered travel network for explorers across Africa and beyond. Share your stories, discover hidden gems, and travel safer.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Who We Are", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "TripBook is a passionate collective of travelers, developers, and cultural enthusiasts who believe in empowering African tourism through technology. We blend storytelling, social networking, and smart travel tools to make your journey unforgettable.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Who We Serve", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "TripBook is built for backpackers, digital nomads, local adventurers, group travelers, and anyone seeking authentic travel experiences in Africa. Whether you're a solo explorer or a tour operator, there's something for you.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("What We Do & How We Do It", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "We connect travelers through interactive maps, user-generated content, and verified reviews. With advanced geolocation, AI-curated suggestions, and seamless integration with local services, TripBook turns your phone into a smart travel companion.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Team Members & Contributors", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "TripBook was born and brought to light by a dedicated team of Africa's finest, Young, Brilliant developers, UI/UX designers, travel bloggers, and open-source contributors from across Africa. We welcome community participation—submit your stories, report issues, or help us improve!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Why TripBook Matters", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Africa's travel potential is immense—but underrepresented. TripBook is here to help bridge the gap by spotlighting local destinations, empowering travelers with information, and building a trust-based travel community like no other.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("The TripBook Model", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Our platform thrives on collaboration. Users earn badges, gain visibility, and even unlock perks by contributing. From adventure itineraries to guide ratings, every piece of content strengthens the travel ecosystem.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Contact Us", style = MaterialTheme.typography.titleMedium)
            Text(
                text = """
                    Email: support@tripbook.africa
                    WhatsApp: +234-800-TRIPBOOK
                    Instagram: @tripbookapp
                    Twitter/X: @tripbook_africa
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
