package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.TripCategory
import com.android.tripbook.repository.CategoryRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository = CategoryRepository.getInstance()
) : ViewModel() {

    val categories: StateFlow<List<TripCategory>> = repository.categories
    val activities: StateFlow<Map<TripCategory, List<String>>> = repository.activities

    // Category CRUD
    fun addCategory(category: TripCategory) {
        viewModelScope.launch { repository.addCategory(category) }
    }

    fun removeCategory(category: TripCategory) {
        viewModelScope.launch { repository.removeCategory(category) }
    }

    fun updateCategory(oldCategory: TripCategory, newCategory: TripCategory) {
        viewModelScope.launch { repository.updateCategory(oldCategory, newCategory) }
    }

    fun getAllCategories(): List<TripCategory> = repository.getAllCategories()

    // Activity CRUD
    fun addActivity(category: TripCategory, activity: String) {
        viewModelScope.launch { repository.addActivity(category, activity) }
    }

    fun removeActivity(category: TripCategory, activity: String) {
        viewModelScope.launch { repository.removeActivity(category, activity) }
    }

    fun updateActivity(category: TripCategory, oldActivity: String, newActivity: String) {
        viewModelScope.launch { repository.updateActivity(category, oldActivity, newActivity) }
    }

    fun getActivitiesForCategory(category: TripCategory): List<String> {
        return repository.getActivitiesForCategory(category)
    }
}