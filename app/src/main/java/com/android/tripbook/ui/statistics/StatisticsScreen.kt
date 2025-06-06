package com.android.tripbook.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.components.CircularBudgetChart
import com.android.tripbook.ui.components.StatisticsCard
import com.android.tripbook.ui.theme.TripBookColors

@Composable
fun StatisticsScreen(
    onMonthSelect: (String) -> Unit = {},
    onStatisticsCardClick: () -> Unit = {},
    onViewAllBudgetPlan: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onViewAllTransactions: () -> Unit = {}
) {
    var selectedMonth by remember { mutableStateOf("June 2023") }
    
    Surface(
        color = TripBookColors.Background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Budget Statistics",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TripBookColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            // Month Selector
            item {
                MonthSelector(
                    selectedMonth = selectedMonth,
                    onMonthSelect = { 
                        selectedMonth = it
                        onMonthSelect(it)
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Budget Overview
            item {
                Text(
                    text = "Budget Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = TripBookColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CircularBudgetChart(
                        progress = 0.65f,
                        label = "₣ 25,000",
                        progressColor = TripBookColors.ButtonPrimary,
                        trackColor = TripBookColors.ChipBackground
                    )
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        BudgetInfoItem(
                            label = "Total Budget",
                            value = "₣ 25,000",
                            color = TripBookColors.ButtonPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BudgetInfoItem(
                            label = "Spent",
                            value = "₣ 16,250",
                            color = TripBookColors.Secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BudgetInfoItem(
                            label = "Remaining",
                            value = "₣ 8,750",
                            color = TripBookColors.Success
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Statistics Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        color = TripBookColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatisticsCard(
                        title = "Avg. Daily",
                        amount = "₣ 541",
                        modifier = Modifier.weight(1f),
                        onClick = onStatisticsCardClick
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatisticsCard(
                        title = "Most Spent",
                        amount = "Accommodation",
                        modifier = Modifier.weight(1f),
                        onClick = onStatisticsCardClick
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Budget Plan
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Budget Plan",
                        style = MaterialTheme.typography.titleMedium,
                        color = TripBookColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    TextButton(onClick = onViewAllBudgetPlan) {
                        Text(
                            text = "View All",
                            color = TripBookColors.ButtonPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View All",
                            tint = TripBookColors.ButtonPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                BudgetCategoryList(onAddCategory = onAddCategory)
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Recent Transactions
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        color = TripBookColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    TextButton(onClick = onViewAllTransactions) {
                        Text(
                            text = "View All",
                            color = TripBookColors.ButtonPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View All",
                            tint = TripBookColors.ButtonPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                RecentTransactionsList()
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MonthSelector(
    selectedMonth: String,
    onMonthSelect: (String) -> Unit
) {
    val months = listOf("April 2023", "May 2023", "June 2023", "July 2023")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        months.forEach { month ->
            MonthChip(
                month = month,
                isSelected = month == selectedMonth,
                onClick = { onMonthSelect(month) }
            )
        }
    }
}

@Composable
fun MonthChip(
    month: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) TripBookColors.ButtonPrimary else TripBookColors.ChipBackground,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = month,
            color = if (isSelected) Color.White else TripBookColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun BudgetInfoItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TripBookColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun BudgetCategoryList(
    onAddCategory: () -> Unit
) {
    val categories = listOf(
        Triple("Accommodation", "₣ 8,000", 0.75f),
        Triple("Food", "₣ 5,000", 0.6f),
        Triple("Transportation", "₣ 4,000", 0.45f),
        Triple("Activities", "₣ 6,000", 0.3f)
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TripBookColors.CardBackground, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        categories.forEach { (category, amount, progress) ->
            BudgetCategoryItem(
                category = category,
                amount = amount,
                progress = progress
            )
            if (category != categories.last().first) {
                Divider(
                    color = TripBookColors.Divider,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
        
        Divider(
            color = TripBookColors.Divider,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAddCategory),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Category",
                tint = TripBookColors.ButtonPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Add Category",
                color = TripBookColors.ButtonPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun BudgetCategoryItem(
    category: String,
    amount: String,
    progress: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                color = TripBookColors.TextPrimary
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TripBookColors.TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = progress,
            color = TripBookColors.ButtonPrimary,
            trackColor = TripBookColors.ChipBackground,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RecentTransactionsList() {
    val transactions = listOf(
        Triple("Hotel Booking", "₣ 2,500", "June 15"),
        Triple("Restaurant", "₣ 120", "June 14"),
        Triple("Train Tickets", "₣ 85", "June 12")
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TripBookColors.CardBackground, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        transactions.forEach { (title, amount, date) ->
            TransactionItem(
                title = title,
                amount = amount,
                date = date
            )
            if (title != transactions.last().first) {
                Divider(
                    color = TripBookColors.Divider,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    title: String,
    amount: String,
    date: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TripBookColors.TextPrimary
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = TripBookColors.TextSecondary
            )
        }
        
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TripBookColors.TextPrimary
        )
    }
}
