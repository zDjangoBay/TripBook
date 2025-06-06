package com.android.tripbook.utils

import java.text.NumberFormat
import java.util.*

/**
 * Utility class for formatting currency amounts in CFA Franc (XAF)
 * for the Cameroonian market
 */
object CurrencyUtils {
    
    /**
     * Formats an amount in CFA Franc with proper thousands separators
     * Example: 150000 -> "150,000 CFA"
     */
    fun formatCFA(amount: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.FRENCH)
        return "${numberFormat.format(amount)} CFA"
    }
    
    /**
     * Formats an amount in CFA Franc with proper thousands separators
     * Example: 150000 -> "150,000 CFA"
     */
    fun formatCFA(amount: Int): String {
        return formatCFA(amount.toDouble())
    }
    
    /**
     * Formats an amount in CFA Franc with proper thousands separators
     * Example: 150000 -> "150,000 CFA"
     */
    fun formatCFA(amount: Long): String {
        return formatCFA(amount.toDouble())
    }
    
    /**
     * Gets typical budget ranges for different types of trips in Cameroon
     */
    object BudgetRanges {
        const val DOMESTIC_SHORT_MIN = 25000      // 1-2 days domestic
        const val DOMESTIC_SHORT_MAX = 75000
        
        const val DOMESTIC_MEDIUM_MIN = 75000     // 3-7 days domestic  
        const val DOMESTIC_MEDIUM_MAX = 200000
        
        const val DOMESTIC_LONG_MIN = 200000      // 7+ days domestic
        const val DOMESTIC_LONG_MAX = 500000
        
        const val REGIONAL_MIN = 300000           // Regional (Central Africa)
        const val REGIONAL_MAX = 800000
        
        const val INTERNATIONAL_MIN = 500000      // International travel
        const val INTERNATIONAL_MAX = 2000000
    }
    
    /**
     * Gets suggested budget categories for Cameroonian travel
     */
    object DefaultCategories {
        val ACCOMMODATION = "Hébergement" to 40000
        val FOOD = "Nourriture" to 25000  
        val TRANSPORT = "Transport" to 30000
        val ACTIVITIES = "Activités" to 20000
        val SHOPPING = "Achats" to 15000
        val EMERGENCY = "Urgence" to 10000
    }
} 