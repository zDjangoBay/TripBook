package com.android.tripbook.utils

import com.android.tripbook.model.BudgetCategory
import com.android.tripbook.model.Expense
import com.android.tripbook.repository.BudgetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Helper class for adding sample expense tracker data for testing purposes
 * Provides realistic Cameroon travel expenses in CFA Franc
 */
object SampleDataHelper {
    
    fun addSampleBudgetData(budgetRepository: BudgetRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            addKribiBeachData(budgetRepository)
            addWazaSafariData(budgetRepository)
            addDoualaBusinessData(budgetRepository)
        }
    }
    
    private suspend fun addKribiBeachData(repository: BudgetRepository) {
        val tripId = "sample_trip_123"
        
        // Categories for Kribi Beach Trip
        val accommodation = BudgetCategory(tripId = tripId, name = "Accommodation", plannedAmount = 60000.0)
        val food = BudgetCategory(tripId = tripId, name = "Food & Dining", plannedAmount = 40000.0)
        val transport = BudgetCategory(tripId = tripId, name = "Transportation", plannedAmount = 35000.0)
        val activities = BudgetCategory(tripId = tripId, name = "Activities & Entertainment", plannedAmount = 15000.0)
        
        // Insert categories first
        repository.insertBudgetCategory(accommodation)
        repository.insertBudgetCategory(food)
        repository.insertBudgetCategory(transport)
        repository.insertBudgetCategory(activities)
        
        val baseTime = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L // Trip start time
        
        // Note: For actual implementation, you'd need to get the real category IDs after insertion
        // Using placeholder IDs for demonstration (1, 2, 3, 4 for this trip)
        
        // Accommodation expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = 1, 
            description = "Hotel Ilomba Kribi (2 nights)", amount = 25000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 1,
            description = "Beachfront Resort (3 nights)", amount = 35000.0, date = baseTime + 2 * 24 * 60 * 60 * 1000L))
        
        // Food & Dining expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = 2,
            description = "Restaurant Le Phare - Seafood dinner", amount = 8500.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 2,
            description = "Local Market Food", amount = 3200.0, date = baseTime + 2 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 2,
            description = "Beach Bar Drinks", amount = 6800.0, date = baseTime + 3 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 2,
            description = "Grilled Fish at Beach", amount = 4500.0, date = baseTime + 4 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 2,
            description = "Hotel Breakfast (3 days)", amount = 7000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        
        // Transportation expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = 3,
            description = "Taxi to Kribi from Douala", amount = 15000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 3,
            description = "Local Transport - Motorcycle taxis", amount = 8000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 3,
            description = "Return Transport - Bus to Yaoundé", amount = 12000.0, date = baseTime + 6 * 24 * 60 * 60 * 1000L))
        
        // Activities expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = 4,
            description = "Beach Excursion - Boat trip", amount = 7500.0, date = baseTime + 2 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 4,
            description = "Nightclub Entry - Kribi nightlife", amount = 2500.0, date = baseTime + 3 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = 4,
            description = "Souvenir Shopping - Local crafts", amount = 5000.0, date = baseTime + 5 * 24 * 60 * 60 * 1000L))
    }
    
    private suspend fun addWazaSafariData(repository: BudgetRepository) {
        val tripId = "sample_trip_456"
        
        // Categories for Waza Safari Trip
        val parkFees = BudgetCategory(tripId = tripId, name = "Park & Tour Fees", plannedAmount = 80000.0)
        val accommodation = BudgetCategory(tripId = tripId, name = "Accommodation", plannedAmount = 70000.0)
        val food = BudgetCategory(tripId = tripId, name = "Food & Supplies", plannedAmount = 30000.0)
        val transport = BudgetCategory(tripId = tripId, name = "Transportation", plannedAmount = 20000.0)
        
        val parkFeesId = repository.insertBudgetCategoryAndGetId(parkFees)
        val accommodationId = repository.insertBudgetCategoryAndGetId(accommodation)
        val foodId = repository.insertBudgetCategoryAndGetId(food)
        val transportId = repository.insertBudgetCategoryAndGetId(transport)
        
        val baseTime = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L // Trip start time
        
        // Park & Tour expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = parkFeesId,
            description = "Waza Park Entry (3 people)", amount = 15000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = parkFeesId,
            description = "Safari Guide (5 days)", amount = 45000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = parkFeesId,
            description = "Photography Permit", amount = 5000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = parkFeesId,
            description = "Wildlife Viewing Equipment", amount = 15000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        
        // Accommodation expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = accommodationId,
            description = "Safari Lodge Maroua (2 nights)", amount = 30000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = accommodationId,
            description = "Camping at Waza (3 nights)", amount = 20000.0, date = baseTime + 2 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = accommodationId,
            description = "Hotel in Garoua (2 nights)", amount = 20000.0, date = baseTime + 5 * 24 * 60 * 60 * 1000L))
        
        // Food expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = foodId,
            description = "Safari Meals - Packed lunches", amount = 18000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = foodId,
            description = "Local Restaurant - Northern cuisine", amount = 7500.0, date = baseTime + 3 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = foodId,
            description = "Water & Snacks", amount = 4500.0, date = baseTime + 2 * 24 * 60 * 60 * 1000L))
        
        // Transportation expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = transportId,
            description = "4WD Vehicle Rental - Safari fuel", amount = 12000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = transportId,
            description = "Local Transport", amount = 8000.0, date = baseTime))
    }
    
    private suspend fun addDoualaBusinessData(repository: BudgetRepository) {
        val tripId = "sample_trip_789"
        
        // Categories for Douala Business Trip
        val accommodation = BudgetCategory(tripId = tripId, name = "Accommodation", plannedAmount = 35000.0)
        val meals = BudgetCategory(tripId = tripId, name = "Meals", plannedAmount = 20000.0)
        val transport = BudgetCategory(tripId = tripId, name = "Transportation", plannedAmount = 15000.0)
        val business = BudgetCategory(tripId = tripId, name = "Business Expenses", plannedAmount = 5000.0)
        
        val accommodationId = repository.insertBudgetCategoryAndGetId(accommodation)
        val mealsId = repository.insertBudgetCategoryAndGetId(meals)
        val transportId = repository.insertBudgetCategoryAndGetId(transport)
        val businessId = repository.insertBudgetCategoryAndGetId(business)
        
        val baseTime = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L // Trip start time
        
        // Accommodation expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = accommodationId,
            description = "Hotel Akwa Palace (1 night)", amount = 18000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = accommodationId,
            description = "Business Hotel Bonanjo (1 night)", amount = 17000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        
        // Meals expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = mealsId,
            description = "Business Lunch - Restaurant La Fourchette", amount = 8000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        repository.insertExpense(Expense(tripId = tripId, categoryId = mealsId,
            description = "Airport Meal", amount = 4500.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = mealsId,
            description = "Hotel Breakfast (2 days)", amount = 7500.0, date = baseTime))
        
        // Transportation expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = transportId,
            description = "Airport Taxi (round trip)", amount = 6000.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = transportId,
            description = "Business Meetings Transport", amount = 9000.0, date = baseTime + 1 * 24 * 60 * 60 * 1000L))
        
        // Business expenses
        repository.insertExpense(Expense(tripId = tripId, categoryId = businessId,
            description = "Meeting Materials", amount = 2500.0, date = baseTime))
        repository.insertExpense(Expense(tripId = tripId, categoryId = businessId,
            description = "Communication - Mobile credit", amount = 2500.0, date = baseTime))
    }
    
    // Extension function to insert and return ID (you may need to add this to your repository)
    private suspend fun BudgetRepository.insertBudgetCategoryAndGetId(category: BudgetCategory): Long {
        insertBudgetCategory(category)
        // Note: This is a simplified approach. In practice, you'd want to return the actual ID from insert
        // For now, we'll use placeholder IDs or modify the repository to return IDs
        return System.currentTimeMillis() % 10000 // Placeholder ID generation
    }
} 