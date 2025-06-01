package com.android.tripbook.screens.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R
import com.android.tripbook.ui.theme.Primary
import com.android.tripbook.ui.theme.Secondary
import com.android.tripbook.ui.theme.Surface

@Composable
fun LandingScreen(
    navigateToLogin: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToDiscovery: () -> Unit
) {
    // Check if user is already logged in
    val isLoggedIn = remember { mutableStateOf(false) }
    
    // If user is logged in, navigate directly to Discovery screen
    LaunchedEffect(isLoggedIn.value) {
        if (isLoggedIn.value) {
            navigateToDiscovery()
        }
    }
    
    // Landing screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with logo and tagline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Replace with your logo
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "TripBook Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                )
                
                Text(
                    text = "TripBook",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Surface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Explore Africa & Beyond",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Surface,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
            
            // Middle section with feature highlights
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FeatureItem(
                    title = "Share Your Journeys",
                    description = "Document and share your travel experiences with fellow adventurers"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                FeatureItem(
                    title = "Discover Hidden Gems",
                    description = "Explore unique destinations through community recommendations"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                FeatureItem(
                    title = "Connect with Travelers",
                    description = "Build a network of like-minded explorers around the world"
                )
            }
            
            // Bottom section with buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = navigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = navigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = navigateToDiscovery
                ) {
                    Text(
                        text = "Explore as Guest",
                        fontSize = 14.sp,
                        color = Surface
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(title: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Surface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = Surface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}
