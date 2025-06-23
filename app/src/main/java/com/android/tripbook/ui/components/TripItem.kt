package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.android.tripbook.model.Triphome

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
        shape = RoundedCornerShape(12.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (logo, companyName, fromLabel, fromCode, dashLine, duration,
                toLabel, toCode, star, rating, price) = createRefs()

            // Company Logo
            AsyncImage(
                model = trip.companyLogo,
                contentDescription = "Company Logo",
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .constrainAs(logo) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(parent.top, margin = 8.dp)
                    },
                contentScale = ContentScale.Crop
            )

            // Company Name
            Text(
                text = trip.companyName ?: "Delta Airlines",
                color = Color(0xFF1565C0), // dark_blue color
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(companyName) {
                    start.linkTo(logo.end, margin = 16.dp)
                    top.linkTo(logo.top)
                    bottom.linkTo(logo.bottom)
                }
            )

            // Dash Line (centered)
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Color.Gray)
                    .constrainAs(dashLine) {
                        centerHorizontallyTo(parent)
                        top.linkTo(logo.bottom, margin = 32.dp)
                    }
            )

            // FROM Label
            Text(
                text = "FROM",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(fromLabel) {
                    start.linkTo(parent.start)
                    end.linkTo(dashLine.start)
                    top.linkTo(logo.bottom, margin = 16.dp)
                }
            )

            // FROM Code
            Text(
                text = trip.fromshort ?: "JFK",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(fromCode) {
                    start.linkTo(fromLabel.start)
                    top.linkTo(fromLabel.bottom)
                }
            )

            // Duration (centered on dash line)
            Text(
                text = trip.arriveTime ?: "2h 45m",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(duration) {
                    centerHorizontallyTo(dashLine)
                    top.linkTo(logo.bottom)
                }
            )

            // TO Label
            Text(
                text = "TO",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(toLabel) {
                    start.linkTo(dashLine.end)
                    end.linkTo(parent.end)
                    top.linkTo(fromLabel.top)
                }
            )

            // TO Code
            Text(
                text = trip.toshort ?: "LAX",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(toCode) {
                    centerHorizontallyTo(toLabel)
                    top.linkTo(toLabel.bottom)
                }
            )

            // Star Icon
            Icon(
                painter = painterResource(android.R.drawable.btn_star_big_on),
                contentDescription = "Rating",
                tint = Color(0xFFFFD700), // Gold color
                modifier = Modifier
                    .size(20.dp)
                    .constrainAs(star) {
                        start.linkTo(parent.start, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    }
            )

            // Rating
            Text(
                text = "${trip.score ?: 4.5}",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(rating) {
                    start.linkTo(star.end, margin = 8.dp)
                    top.linkTo(star.top)
                    bottom.linkTo(star.bottom)
                }
            )

            // Price
            Text(
                text = "$${trip.price ?: "170.6"}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(price) {
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                }
            )
        }
    }
}