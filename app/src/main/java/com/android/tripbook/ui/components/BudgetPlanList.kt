package com.android.tripbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class BudgetPlan(
    val id: Int,
    val title: String,
    val amount: String,
    val description: String = ""
)

@Composable
fun BudgetPlanList(
    plans: List<BudgetPlan>,
    modifier: Modifier = Modifier,
    onViewAll: () -> Unit = {},
    onPlanClick: (BudgetPlan) -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("My Budget Plan", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onViewAll) { Text("View all") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plans) { plan ->
                BudgetPlanCard(plan = plan, onClick = { onPlanClick(plan) })
            }
        }
    }
}

@Composable
fun BudgetPlanCard(
    plan: BudgetPlan,
    onClick: () -> Unit = {}
) {
    Card(
        // Corrected line: clickable should be chained to the Modifier
        modifier = Modifier
            .width(180.dp)
            .height(100.dp)
            .clickable { onClick() }, // Here's the fix!
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(plan.title, style = MaterialTheme.typography.titleSmall)
            Text(plan.amount, style = MaterialTheme.typography.headlineSmall)
            if (plan.description.isNotEmpty()) {
                Text(plan.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}