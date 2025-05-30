package com.android.tripbook.ui.uis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.TripBookTheme

@Composable
fun PlanNewTripScreen(onBackClick: () -> Unit) {
    val selectedTripType = remember { mutableStateOf("Cultural") }

    TripBookTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6B5B95))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Back button and header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Plan New Trip",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Create your African adventure",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Content in white card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {

                        // Trip Name
                        Text(text = "Trip Name", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = "e.g., Egyptian Pyramids Tour",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Destination
                        Text(text = "Destination", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = "e.g., Cairo, Egypt",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Start and End Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Start Date", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "dd/mm/yyyy",
                                        modifier = Modifier.weight(1f),
                                        color = Color.Gray
                                    )
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Calendar",
                                        tint = Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "End Date", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "dd/mm/yyyy",
                                        modifier = Modifier.weight(1f),
                                        color = Color.Gray
                                    )
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Calendar",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Number of Travelers
                        Text(text = "Number of Travelers", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "1 person", modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Budget
                        Text(text = "Budget (USD)", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = "e.g., 2000",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Trip Type
                        Text(text = "Trip Type", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))

                        // First row of trip types
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TripTypeChip(
                                text = "Adventure",
                                isSelected = selectedTripType.value == "Adventure",
                                onClick = { selectedTripType.value = "Adventure" },
                                modifier = Modifier.weight(1f)
                            )
                            TripTypeChip(
                                text = "Cultural",
                                isSelected = selectedTripType.value == "Cultural",
                                onClick = { selectedTripType.value = "Cultural" },
                                modifier = Modifier.weight(1f)
                            )
                            TripTypeChip(
                                text = "Relaxation",
                                isSelected = selectedTripType.value == "Relaxation",
                                onClick = { selectedTripType.value = "Relaxation" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Second row of trip types
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TripTypeChip(
                                text = "Safari",
                                isSelected = selectedTripType.value == "Safari",
                                onClick = { selectedTripType.value = "Safari" },
                                modifier = Modifier.weight(1f)
                            )
                            TripTypeChip(
                                text = "Beach",
                                isSelected = selectedTripType.value == "Beach",
                                onClick = { selectedTripType.value = "Beach" },
                                modifier = Modifier.weight(1f)
                            )
                            // Empty space to balance the layout
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Description
                        Text(text = "Description", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = "Describe your dream trip...",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        // Create Trip Button
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B73FF)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CREATE TRIP",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) Color(0xFF6B73FF) else Color(0xFFF0F0F0)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}