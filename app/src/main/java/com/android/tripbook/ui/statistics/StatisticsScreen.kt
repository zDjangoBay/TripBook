package com.android.tripbook.ui.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class) // This is often needed for Card in Material 3 depending on its features
@Composable
fun StatisticsScreen(
    onMonthSelect: () -> Unit = {},
    onStatisticsCardClick: () -> Unit = {},
    onViewAllBudgetPlan: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onViewAllTransactions: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Statistics", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onMonthSelect) {
                Text("Aug ▼")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards (Actual Cost, Planned Budget)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatisticsCard(
                title = "Actual Cost", 
                amount = "₣ 5,000", 
                onClick = onStatisticsCardClick,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatisticsCard(
                title = "Planned Budget", 
                amount = "₣ 25,000", 
                onClick = onStatisticsCardClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // My Budget Plan Section
        BudgetPlanList(onViewAll = onViewAllBudgetPlan)

        Spacer(modifier = Modifier.height(24.dp))

        // Circular Budget Chart
        CircularBudgetChart()

        Spacer(modifier = Modifier.height(24.dp))

        // Categories Section
        CategorySection(onAddCategory = onAddCategory)

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Transactions Section
        RecentTransactionsList(onViewAll = onViewAllTransactions)
    }
}

// Placeholder composables for each section
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsCard(
    title: String, 
    amount: String, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = amount, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun BudgetPlanList(onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("My Budget Plan", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onViewAll) { Text("View all") }
    }
    // TODO: List planned activities
}

@Composable
fun CircularBudgetChart() {
    // TODO: Implement circular chart
    Text("Budget Management Planned (Chart Placeholder)", modifier = Modifier.padding(8.dp))
}

@Composable
fun CategorySection(onAddCategory: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onAddCategory) { Text("Add more") }
    }
    // TODO: List categories
}

@Composable
fun RecentTransactionsList(onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onViewAll) { Text("View all") }
    }
    // TODO: List recent transactions
}