package com.android.tripbook.ui.triplist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Temporary hardcoded Trip model
data class Trip(
    val title: String,
    val destination: String,
    val dateRange: String,
    val status: String
)


@Composable
fun TripCard(trip: Trip, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trip.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = trip.destination, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = trip.dateRange, fontSize = 12.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trip.status,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(Color(0xFF81C784), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}