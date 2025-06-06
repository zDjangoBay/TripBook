package com.android.tripbook.ui.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonGraphScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparison Graph") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // This actions block is required for TopAppBar in Material 3, even if empty
                }
            )
        }
    ) { innerPadding ->
        Surface(
            color = TripBookColors.Background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Planned vs. Actual Costs by Category",
                    style = MaterialTheme.typography.titleLarge,
                    color = TripBookColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Grouped Bar Chart
                GroupedBarChart()
            }
        }
    }
}

@Composable
fun GroupedBarChart() {
    // Sample data for the chart
    val categories = listOf("Flights", "Accommodation", "Activities", "Food", "Transport")
    val plannedBudgets = listOf(3000f, 1500f, 2000f, 1000f, 800f)
    val actualCosts = listOf(2500f, 1800f, 1500f, 1200f, 600f)

    val maxValue = maxOf(plannedBudgets.maxOrNull() ?: 0f, actualCosts.maxOrNull() ?: 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Y-axis labels and chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₣ ${maxValue.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
                Text(
                    text = "₣ ${(maxValue * 0.75f).toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
                Text(
                    text = "₣ ${(maxValue * 0.5f).toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
                Text(
                    text = "₣ ${(maxValue * 0.25f).toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
                Text(
                    text = "₣ 0",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
            }

            // Bars
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Horizontal grid lines
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(5) {
                        Divider(
                            color = TripBookColors.Divider,
                            thickness = 1.dp
                        )
                        if (it < 4) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Bars
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    categories.forEachIndexed { index, category ->
                        Column(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Planned budget bar (Blue)
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height((plannedBudgets[index] / maxValue * 300).dp)
                                    .background(TripBookColors.ButtonPrimary)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Actual cost bar
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height((actualCosts[index] / maxValue * 300).dp)
                                    .background(TripBookColors.Accent)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Category label
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodySmall,
                                color = TripBookColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(TripBookColors.ButtonPrimary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Planned Budget",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(TripBookColors.Accent)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Actual Cost",
                    style = MaterialTheme.typography.bodySmall,
                    color = TripBookColors.TextSecondary
                )
            }
        }
    }
}