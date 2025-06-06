package com.android.tripbook.repository

import com.android.tripbook.model.TripCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryRepository {
    private val _categories = MutableStateFlow<List<TripCategory>>(emptyList())
    val categories: StateFlow<List<TripCategory>> = _categories.asStateFlow()

    private val _activities = MutableStateFlow<Map<TripCategory, List<String>>>(emptyMap())
    val activities: StateFlow<Map<TripCategory, List<String>>> = _activities.asStateFlow()

    // Category CRUD
    fun addCategory(category: TripCategory) {
        if (!_categories.value.contains(category)) {
            _categories.value = _categories.value + category
        }
    }

    fun removeCategory(category: TripCategory) {
        _categories.value = _categories.value - category
        _activities.value = _activities.value - category
    }

    fun updateCategory(oldCategory: TripCategory, newCategory: TripCategory) {
        _categories.value = _categories.value.map { if (it == oldCategory) newCategory else it }
        _activities.value = _activities.value.let { map ->
            if (map.containsKey(oldCategory)) {
                val activities = map[oldCategory] ?: emptyList()
                map - oldCategory + (newCategory to activities)
            } else map
        }
    }

    fun getAllCategories(): List<TripCategory> = _categories.value

    // Activity CRUD
    fun addActivity(category: TripCategory, activity: String) {
        val current = _activities.value[category] ?: emptyList()
        _activities.value = _activities.value + (category to (current + activity))
    }

    fun removeActivity(category: TripCategory, activity: String) {
        val current = _activities.value[category] ?: emptyList()
        _activities.value = _activities.value + (category to (current - activity))
    }

    fun updateActivity(category: TripCategory, oldActivity: String, newActivity: String) {
        val current = _activities.value[category] ?: emptyList()
        _activities.value = _activities.value + (category to current.map { if (it == oldActivity) newActivity else it })
    }

    fun getActivitiesForCategory(category: TripCategory): List<String> {
        return _activities.value[category] ?: emptyList()
    }

    companion object {
        @Volatile
        private var INSTANCE: CategoryRepository? = null

        fun getInstance(): CategoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CategoryRepository().also { INSTANCE = it }
            }
        }
    }
}