package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape  // <-- Add this import
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.tripbook.Model.Trip
@Composable
fun TripItem(trip: Trip, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            // Airline Logo
            AsyncImage(
                model = trip.companyLogo,
                contentDescription = "Company Logo",
                modifier = Modifier
                    .size(55.dp)
                    .offset(x = 0.dp, y = 0.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            // Airline Name
            Text(
                text = trip.companyName,
                color = Color(0xFF001F54),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 71.dp) // 55dp logo + 16dp spacing
                    .offset(y = 16.dp) // Align with logo vertically
            )

            // Divider Line (Replace with your dash_line drawable)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray)
                    .padding(top = 71.dp) // 55dp logo + 16dp spacing
            )

            // FROM Section
            Column(modifier = Modifier.offset(x = 0.dp, y = 87.dp)) {
                Text(
                    text = "FROM",
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = trip.from,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = trip.fromshort,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            // DURATION Section (Centered)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 87.dp)
            ) {
                Text(
                    text = "DURATION",
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = trip.arriveTime,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // TO Section
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 87.dp)
            ) {
                Text(
                    text = "TO",
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = trip.to,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = trip.toshort,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            // Rating Star
            Icon(
                painter = painterResource(android.R.drawable.btn_star_big_on),
                contentDescription = "Rating",
                tint = Color.Yellow,
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = 8.dp, y = 133.dp)
            )

            // Rating Score
            Text(
                text = trip.score.toString(),
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .offset(x = 40.dp, y = 136.dp)
            )

            // Price
            Text(
                text = "$${trip.price}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .offset(x = (-16).dp, y = 133.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}