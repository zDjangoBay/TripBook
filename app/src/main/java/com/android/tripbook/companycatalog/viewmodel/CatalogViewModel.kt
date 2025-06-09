package com.android.tripbook.companycatalog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.companycatalog.data.SearchHistoryItem
import com.android.tripbook.companycatalog.data.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {
    private val searchHistoryRepository = SearchHistoryRepository(application)
    
    val searchHistory: Flow<List<SearchHistoryItem>> = searchHistoryRepository.searchHistory

    fun addSearchQuery(query: String, resultCount: Int) {
        viewModelScope.launch {
            searchHistoryRepository.addSearchQuery(query, resultCount)
        }
    }

    fun removeSearchQuery(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.removeSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory()
        }
    }
} 