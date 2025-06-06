package com.android.tripbook.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.tripbook.Model.Place
import com.android.tripbook.Model.Triphome
import com.android.tripbook.R
import com.android.tripbook.ui.components.*

@Composable
fun HomeScreen(
    navController: NavController,
    upcomingTrips: List<Triphome>,
    recommendedPlaces: List<Place>,
    isLoadingUpcoming: Boolean,
    isLoadingRecommended: Boolean,
    onTripClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White), // White looks better with black text
        contentPadding = PaddingValues(
            bottom = 100.dp, // Safe space for bottom nav bar
            top = 0.dp
        )
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = stringResource(R.string.Profile_Image),
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.User_Name),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.bell_icon),
                    contentDescription = stringResource(R.string.notification_btn),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.Travelling_Made_Easy),
                fontSize = 25.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TransportOption(R.drawable.bus, R.string.Bus, Color(0xFFE0BBE4), onClick = {navController.navigate("bus_companies")})
                TransportOption(R.drawable.boat, R.string.Boats, Color(0xFFFFE0B2), onClick = {navController.navigate("boat_companies")})
                TransportOption(R.drawable.airplane, R.string.flight, Color(0xFFB3E5FC), onClick = {navController.navigate("airline_companies")})
                TransportOption(R.drawable.train, R.string.Trains, Color(0xFFC8E6C9), onClick = {navController.navigate("train_companies")})
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        item {
            Text(
                text = stringResource(R.string.Upcoming_Schedules),
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        if (isLoadingUpcoming) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            item {
                TripList(
                    trips = upcomingTrips,
                    onTripClick = onTripClick
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                text = stringResource(R.string.Recommendation_Places),
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        item {
            if (isLoadingRecommended) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recommendedPlaces) { place ->
                        PlaceItem(
                            place = place,
                            onClick = { /* Handle click */ }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun TransportOption(
    iconRes: Int,
    labelRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = stringResource(labelRes),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(labelRes),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}