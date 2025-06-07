
/*
- Acts as memory storage and state manager for company data
- Ensures that the UI elements receive realtime update anytime a company is liked
  or retrieved.
 */
package com.android.tripbook.companycatalog.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompanyRepository {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private val _filteredCompanies = MutableStateFlow<List<Company>>(emptyList())
    val filteredCompanies: StateFlow<List<Company>> = _filteredCompanies.asStateFlow()

    private val _selectedCompany = MutableStateFlow<Company?>(null)
    val selectedCompany: StateFlow<Company?> = _selectedCompany.asStateFlow()

    init {
        loadCompanies()
    }

    private fun loadCompanies() {
        val companyList = MockCompanyData.getCameroonCompanies()
        _companies.value = companyList
        _filteredCompanies.value = companyList
    }

    fun searchCompanies(query: String) {
        val filtered = if (query.isBlank()) {
            _companies.value
        } else {
            _companies.value.filter { company ->
                company.name.contains(query, ignoreCase = true) ||
                company.description.contains(query, ignoreCase = true) ||
                company.category.contains(query, ignoreCase = true) ||
                company.location.contains(query, ignoreCase = true)
            }
        }
        _filteredCompanies.value = filtered
    }

    fun filterByCategory(category: String) {
        val filtered = if (category == "All") {
            _companies.value
        } else {
            _companies.value.filter { it.category == category }
        }
        _filteredCompanies.value = filtered
    }

    fun toggleLike(companyId: String) {
        val updatedCompanies = _companies.value.map { company ->
            if (company.id == companyId) {
                company.copy(isLiked = !company.isLiked)
            } else {
                company
            }
        }
        _companies.value = updatedCompanies
        
        // Update filtered list as well
        val updatedFilteredCompanies = _filteredCompanies.value.map { company ->
            if (company.id == companyId) {
                company.copy(isLiked = !company.isLiked)
            } else {
                company
            }
        }
        _filteredCompanies.value = updatedFilteredCompanies

        // Update selected company if it's the one being liked
        _selectedCompany.value?.let { selected ->
            if (selected.id == companyId) {
                _selectedCompany.value = selected.copy(isLiked = !selected.isLiked)
            }
        }
    }

    fun getCompanyById(id: String): Company? {
        return _companies.value.find { it.id == id }
    }

    fun selectCompany(company: Company) {
        _selectedCompany.value = company
    }

    fun clearSelection() {
        _selectedCompany.value = null
    }

    fun getCategories(): List<String> {
        return listOf("All") + _companies.value.map { it.category }.distinct().sorted()
    }

    fun getLikedCompanies(): List<Company> {
        return _companies.value.filter { it.isLiked }
    }
}
