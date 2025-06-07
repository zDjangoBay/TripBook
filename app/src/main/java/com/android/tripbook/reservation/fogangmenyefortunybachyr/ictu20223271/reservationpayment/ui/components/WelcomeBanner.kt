package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Flight
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
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.R
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.animation.AnimationUtils
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.AppIcons
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.ui.theme.TextSecondary


/**
 * Welcome banner with illustration and call-to-action
 */
@Composable
fun WelcomeBanner(
    onBookNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (expanded) 200.dp else 80.dp)
                .clickable { expanded = !expanded }
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.travel_illustration_1),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6200EE).copy(alpha = 0.8f),  // Purple
                                Color(0xFFFF8800).copy(alpha = 0.8f)   // Orange
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title always visible
                Text(
                    text = "Book Your Next Adventure",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = if (expanded) 24.sp else 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                // Expanded content
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Discover amazing destinations and create unforgettable memories",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onBookNowClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            modifier = Modifier.then(AnimationUtils.pulseAnimation(pulseFraction = 1.05f, duration = 2000))
                        ) {
                            Icon(
                                imageVector = AppIcons.FlightTakeoff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2500))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Book Now",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.then(AnimationUtils.waveAnimation(amplitude = 3f, frequency = 0.3f))
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section header with illustration
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    illustrationResId: Int,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { isHovered = !isHovered },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Section text with slide-in animation
        Column(
            modifier = Modifier
                .weight(1f)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else TextSecondary
            )

            // Extra info that appears when hovered
            AnimatedVisibility(
                visible = isHovered,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = "Tap to explore your ${title.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Illustration with combined animations
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = null,
            modifier = Modifier
                .size(if (isHovered) 70.dp else 60.dp)
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (isHovered)
                        AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000)
                    else
                        AnimationUtils.floatingAnimation(offsetY = 5, duration = 3000)
                )
                .animateContentSize(),
            contentScale = ContentScale.Crop
        )
    }
}
