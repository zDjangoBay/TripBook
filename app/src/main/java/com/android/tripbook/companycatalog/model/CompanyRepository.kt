/*
- Acts as memory storage and state manager for company data
- Ensures that the UI elements receive realtime update anytime a company is liked
  or retrieved.
 */
package com.android.tripbook.companycatalog.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.tripbook.companycatalog.model.MockCompanyData

// Singleton object to manage company data
object CompanyRepository {

    private val _companies = mutableStateListOf<Company>().apply {
        addAll(MockCompanyData.companies) // Initialize with mock data
    }

    val companies: SnapshotStateList<Company> = _companies

    /**
     * Toggles the like status of a company and updates its like count.
     * This method directly modifies the Company object within the observable list.
     *
     * @param companyId The ID of the company to update.
     * @param newIsLiked The new like status (true if liked, false if unliked).
     */
    fun toggleLike(companyId: Int, newIsLiked: Boolean) {
        val index = _companies.indexOfFirst { it.id == companyId }
        if (index != -1) {
            val company = _companies[index]
            _companies[index] = company.copy(
                likes = if (newIsLiked) company.likes + 1 else company.likes - 1,
                isLiked = newIsLiked // Update the isLiked property
            )
        }
    }

    /**
     * Gets a company by its ID.
     * @param id The ID of the company.
     * @return The Company object, or null if not found.
     */
    fun getCompanyById(id: Int): Company? {
        return _companies.find { it.id == id }
    }
}
