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
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            // Create references for all elements (matching XML IDs)
            val (logo, companyTxt, imageView4, fromTxt, fromshortTxt, arrivalTxt,
                toTxt, toShortTxt, priceTxt, imageView3, scoreTxt) = createRefs()

            // Logo (matching XML: 55dp size, 8dp margins from start and top)
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

            // Company Name (matching XML: 16dp margin from logo)
            Text(
                text = trip.companyName,
                color = Color(0xFF001F54),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(companyTxt) {
                    start.linkTo(logo.end, margin = 16.dp)
                    top.linkTo(logo.top)
                    bottom.linkTo(logo.bottom)
                }
            )

            // Dash Line (matching XML: centered horizontally, 32dp below logo)
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(1.dp)
                    .background(Color.Gray)
                    .constrainAs(imageView4) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(logo.bottom, margin = 32.dp)
                    }
            )

            // FROM Label (matching XML: 16dp below logo, positioned to left of dash line)
            Text(
                text = "FROM",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.constrainAs(fromTxt) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(logo.bottom, margin = 16.dp)
                }
            )

            // FROM Short Code (matching XML: aligned with FROM label start)
            Text(
                text = trip.fromshort ?: "NYC",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(fromshortTxt) {
                    start.linkTo(fromTxt.start)
                    top.linkTo(fromTxt.bottom, margin = 2.dp)
                }
            )

            // Arrival Time (matching XML: centered on dash line, same top as FROM)
            Text(
                text = trip.arriveTime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(arrivalTxt) {
                    start.linkTo(imageView4.start)
                    end.linkTo(imageView4.end)
                    top.linkTo(logo.bottom, margin = 16.dp)
                }
            )

            // TO Label (matching XML: positioned to right of dash line)
            Text(
                text = "TO",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.constrainAs(toTxt) {
                    end.linkTo(parent.end, margin = 16.dp)
                    top.linkTo(fromTxt.top)
                }
            )

            // TO Short Code (matching XML: centered with TO label)
            Text(
                text = trip.toshort ?: "LAX",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(toShortTxt) {
                    end.linkTo(toTxt.end)
                    top.linkTo(toTxt.bottom, margin = 2.dp)
                }
            )

            // Star Icon (matching XML: 16dp from start and bottom)
            Icon(
                painter = painterResource(android.R.drawable.btn_star_big_on),
                contentDescription = "Rating",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(imageView3) {
                        start.linkTo(parent.start, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    }
            )

            // Rating Score (matching XML: 8dp from star)
            Text(
                text = trip.score.toString(),
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(scoreTxt) {
                    start.linkTo(imageView3.end, margin = 8.dp)
                    top.linkTo(imageView3.top)
                    bottom.linkTo(imageView3.bottom)
                }
            )

            // Price (matching XML: 16dp from end, 8dp from bottom)
            Text(
                text = "$${trip.price}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.constrainAs(priceTxt) {
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                }
            )
        }
    }
}