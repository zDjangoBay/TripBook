package com.android.tripbook.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.utils.CurrencyUtils
import com.android.tripbook.viewmodel.BudgetViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BudgetSummaryCard(
    categories: List<BudgetCategory>,
    expenses: List<Expense>,
    tripBudget: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Budget Summary",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF2D3748)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Track your expenses for this amazing trip",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            val totalPlanned = categories.sumOf { it.plannedAmount }
            val totalSpent = expenses.sumOf { it.amount }
            val remaining = tripBudget - totalSpent
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetInfoItem(
                    icon = "💰",
                    title = "Total Budget",
                    amount = CurrencyUtils.formatCFA(tripBudget)
                )
                BudgetInfoItem(
                    icon = "💳",
                    title = "Spent",
                    amount = CurrencyUtils.formatCFA(totalSpent.toInt())
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetInfoItem(
                    icon = "📊",
                    title = "Planned",
                    amount = CurrencyUtils.formatCFA(totalPlanned.toInt())
                )
                BudgetInfoItem(
                    icon = if (remaining >= 0) "✅" else "⚠️",
                    title = "Remaining",
                    amount = CurrencyUtils.formatCFA(remaining.toInt()),
                    textColor = if (remaining >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun BudgetInfoItem(
    icon: String,
    title: String,
    amount: String,
    textColor: Color = Color(0xFF374151)
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = textColor
        )
    }
}

@Composable
fun EmptyStateCard(onAddCategoryClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🏷️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Budget Categories Yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start by creating categories like Transport, Accommodation, Food, and Activities",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAddCategoryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667EEA)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create First Category")
            }
        }
    }
}

@Composable
fun BudgetCategoryCard(
    category: BudgetCategory,
    expenses: List<Expense>,
    onAddExpenseClick: () -> Unit,
    onEditCategoryClick: () -> Unit,
    onDeleteCategoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF1F2937)
                )
                Row {
                    IconButton(
                        onClick = onEditCategoryClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF667EEA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteCategoryClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val totalSpent = expenses.sumOf { it.amount }
            val progress = if (category.plannedAmount > 0) (totalSpent / category.plannedAmount).toFloat() else 0f
            val progressColor = when {
                progress <= 0.7f -> Color(0xFF10B981)
                progress <= 0.9f -> Color(0xFFF59E0B)
                else -> Color(0xFFEF4444)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${CurrencyUtils.formatCFA(totalSpent.toInt())} / ${CurrencyUtils.formatCFA(category.plannedAmount.toInt())}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = progressColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = Color(0xFFE5E7EB)
            )
            
            if (expenses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recent Expenses",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                expenses.take(3).forEach { expense ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = expense.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(expense.date)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Text(
                            text = CurrencyUtils.formatCFA(expense.amount.toInt()),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF1F2937)
                        )
                    }
                    if (expense != expenses.take(3).last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                if (expenses.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${expenses.size - 3} more expenses",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAddExpenseClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667EEA)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Expense",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun OverviewTabContent(
    categories: List<BudgetCategory>,
    expenses: List<Expense>,
    tripBudget: Int,
    onAddCategoryClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            // Budget Summary Card
            BudgetSummaryCard(
                categories = categories,
                expenses = expenses,
                tripBudget = tripBudget
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Trip Overview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OverviewStatItem("📅", "Duration", "7 days")
                        OverviewStatItem("🏷️", "Categories", "${categories.size}")
                        OverviewStatItem("💳", "Expenses", "${expenses.size}")
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewStatItem(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Color(0xFF374151)
        )
    }
}

@Composable
fun CategoriesTabContent(
    categories: List<BudgetCategory>,
    expenses: List<Expense>,
    onAddCategoryClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onDeleteCategoryClick: (BudgetCategory) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        if (categories.isEmpty()) {
            item {
                EmptyStateCard(onAddCategoryClick = onAddCategoryClick)
            }
        } else {
            items(categories) { category ->
                BudgetCategoryCard(
                    category = category,
                    expenses = expenses.filter { it.categoryId == category.id },
                    onAddExpenseClick = onAddExpenseClick,
                    onEditCategoryClick = { /* TODO */ },
                    onDeleteCategoryClick = { onDeleteCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
fun ExpensesTabContent(
    expenses: List<Expense>,
    categories: List<BudgetCategory>,
    onAddExpenseClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        if (expenses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "💳", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Expenses Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start tracking your trip expenses to stay within budget",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onAddExpenseClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add First Expense")
                        }
                    }
                }
            }
        } else {
            // Group expenses by category
            val expensesByCategory = expenses.groupBy { expense ->
                categories.find { it.id == expense.categoryId }?.name ?: "Unknown"
            }
            
            expensesByCategory.forEach { (categoryName, categoryExpenses) ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color(0xFF1F2937)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            categoryExpenses.forEach { expense ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = expense.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF374151)
                                        )
                                        Text(
                                            text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(expense.date)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    Text(
                                        text = CurrencyUtils.formatCFA(expense.amount.toInt()),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsTabContent(
    categories: List<BudgetCategory>,
    expenses: List<Expense>,
    tripBudget: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            // Analytics Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Spending Analytics",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    val totalSpent = expenses.sumOf { it.amount }
                    val totalPlanned = categories.sumOf { it.plannedAmount }
                    val progress = if (tripBudget > 0) (totalSpent / tripBudget).toFloat() else 0f
                    
                    Text(
                        text = "Budget Usage",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { progress.coerceAtMost(1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = when {
                            progress <= 0.7f -> Color(0xFF10B981)
                            progress <= 0.9f -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        },
                        trackColor = Color(0xFFE5E7EB)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% of budget used",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Category breakdown
                    Text(
                        text = "Spending by Category",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    categories.forEach { category ->
                        val categoryExpenses = expenses.filter { it.categoryId == category.id }
                        val categorySpent = categoryExpenses.sumOf { it.amount }
                        val categoryProgress = if (category.plannedAmount > 0) (categorySpent / category.plannedAmount).toFloat() else 0f
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF374151)
                                )
                                LinearProgressIndicator(
                                    progress = { categoryProgress.coerceAtMost(1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = when {
                                        categoryProgress <= 0.7f -> Color(0xFF10B981)
                                        categoryProgress <= 0.9f -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    },
                                    trackColor = Color(0xFFE5E7EB)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = CurrencyUtils.formatCFA(categorySpent.toInt()),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF374151)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripBudgetScreen(
    budgetViewModel: BudgetViewModel,
    tripId: String,
    navController: NavController
) {
    val categories by budgetViewModel.budgetCategoriesForTrip.observeAsState(emptyList())
    val expenses by budgetViewModel.expensesForTrip.observeAsState(emptyList())
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf<BudgetCategory?>(null) }
    var selectedTab by remember { mutableStateOf("Overview") }

    if (showAddCategoryDialog) {
        AddEditBudgetCategoryDialog(
            budgetViewModel = budgetViewModel,
            tripId = tripId,
            onDismiss = { showAddCategoryDialog = false }
        )
    }
    
    if (showAddExpenseDialog) {
        AddEditExpenseDialog(
            budgetViewModel = budgetViewModel,
            tripId = tripId,
            onDismiss = { showAddExpenseDialog = false }
        )
    }

    showDeleteCategoryDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = null },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete '${category.name}'?") },
            confirmButton = {
                Button(onClick = {
                    budgetViewModel.deleteBudgetCategory(category)
                    showDeleteCategoryDialog = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCategoryDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Purple gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                )
        )
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Budget Tracker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Manage your trip expenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Overview", "Categories", "Expenses", "Analytics").forEach { tab ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = tab },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (selectedTab == tab) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (selectedTab == tab) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(3.dp)
                                    .background(
                                        Color.White,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Content
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Show different content based on selected tab
                    when (selectedTab) {
                        "Overview" -> OverviewTabContent(
                            categories = categories,
                            expenses = expenses,
                            tripBudget = 200000,
                            onAddCategoryClick = { showAddCategoryDialog = true }
                        )
                        "Categories" -> CategoriesTabContent(
                            categories = categories,
                            expenses = expenses,
                            onAddCategoryClick = { showAddCategoryDialog = true },
                            onAddExpenseClick = { showAddExpenseDialog = true },
                            onDeleteCategoryClick = { showDeleteCategoryDialog = it }
                        )
                        "Expenses" -> ExpensesTabContent(
                            expenses = expenses,
                            categories = categories,
                            onAddExpenseClick = { showAddExpenseDialog = true }
                        )
                        "Analytics" -> AnalyticsTabContent(
                            categories = categories,
                            expenses = expenses,
                            tripBudget = 200000
                        )
                    }
                }
            }
        }
        
        // Floating Action Button
        if (categories.isNotEmpty()) {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = Color(0xFF667EEA),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }
        }
    }
} 