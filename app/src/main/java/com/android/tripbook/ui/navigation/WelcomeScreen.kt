package com.android.tripbook.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Travel hero image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF5722).copy(alpha = 0.7f),
                            Color(0xFF1565C0).copy(alpha = 0.6f),
                            Color(0xFF1976D2).copy(alpha = 0.5f),
                            Color(0xFF2196F3).copy(alpha = 0.3f)
                        )
                    )
                )
        )

        // This permits text to be over the image
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // First line of text
            Text(
                text = "Explore\nThe World",
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 52.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Second line
            Text(
                text = "Manage Your Trips\nwith our App",
                fontSize = 19.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Button
            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Let's Start >",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}