package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.android.tripbook.Model.Triphome

@Composable
fun TripItem(trip: Triphome, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Company Logo (top-left)
            AsyncImage(
                model = trip.companyLogo,
                contentDescription = "Company Logo",
                modifier = Modifier
                    .size(55.dp)
                    .offset(x = 8.dp, y = 8.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            // Company Name (next to logo)
            Text(
                text = trip.companyName,
                color = Color(0xFF001F54), // dark_blue color
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = 79.dp, y = 24.dp) // 8 + 55 + 16 = 79dp from left, centered with logo
            )

            // Dash Line (centered horizontally, below logo)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(1.dp)
                    .background(Color.Gray)
                    .align(Alignment.TopCenter)
                    .offset(y = 95.dp) // 8 + 55 + 32 = 95dp from top
            )

            // FROM Section (left side)
            Column(
                modifier = Modifier
                    .offset(x = 16.dp, y = 79.dp)
                    .width(80.dp)
            ) {
                Text(
                    text = "FROM",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = trip.fromshort,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Arrival Time (center, above dash line)
            Text(
                text = trip.arriveTime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 79.dp)
            )

            // TO Section (right side)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 79.dp)
                    .width(80.dp)
            ) {
                Text(
                    text = "TO",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = trip.toshort,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Star Icon (bottom-left)
            Icon(
                painter = painterResource(android.R.drawable.btn_star_big_on),
                contentDescription = "Rating",
                tint = Color.Unspecified, // Keep original star color
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = (-16).dp)
            )

            // Rating Score (next to star)
            Text(
                text = trip.score.toString(),
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 48.dp, y = (-13).dp) // 16 + 24 + 8 = 48dp from left
            )

            // Price (bottom-right)
            Text(
                text = "$${trip.price}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = (-8).dp)
            )
        }
    }
}