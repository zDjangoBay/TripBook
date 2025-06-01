package com.android.tripbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.TravelAgencyRepository
import com.android.tripbook.data.model.TravelAgency
import com.android.tripbook.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.NoSuchElementException

/**
 * ViewModel for travel agency operations
 */
class TravelAgencyViewModel(private val repository: TravelAgencyRepository) : ViewModel() {
    
    // All agencies
    private val _agenciesState = MutableLiveData<Resource<List<TravelAgency>>>()
    val agenciesState: LiveData<Resource<List<TravelAgency>>> = _agenciesState
    
    // Single agency details
    private val _agencyState = MutableLiveData<Resource<TravelAgency>>()
    val agencyState: LiveData<Resource<TravelAgency>> = _agencyState
    
    // Search results
    private val _searchState = MutableLiveData<Resource<List<TravelAgency>>>()
    val searchState: LiveData<Resource<List<TravelAgency>>> = _searchState
    
    /**
     * Load all travel agencies
     */
    fun loadAllAgencies() {
        _agenciesState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                repository.getAllAgencies().collectLatest { agencies ->
                    _agenciesState.value = Resource.Success(agencies)
                }
            } catch (e: Exception) {
                _agenciesState.value = Resource.Error("Failed to load agencies: ${e.message}")
            }
        }
    }
    
    /**
     * Load a specific travel agency by ID
     */
    fun loadAgency(agencyId: String) {
        _agencyState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val agency = repository.getAgencyById(agencyId)
                _agencyState.value = Resource.Success(agency)
            } catch (e: NoSuchElementException) {
                _agencyState.value = Resource.Error("Travel agency not found")
            } catch (e: Exception) {
                _agencyState.value = Resource.Error("Error loading agency: ${e.message}")
            }
        }
    }
    
    /**
     * Search for travel agencies by name or description
     */
    fun searchAgencies(query: String) {
        if (query.isEmpty()) {
            _searchState.value = Resource.Success(emptyList())
            return
        }
        
        _searchState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                repository.searchAgencies(query).collectLatest { agencies ->
                    _searchState.value = Resource.Success(agencies)
                }
            } catch (e: Exception) {
                _searchState.value = Resource.Error("Error during search: ${e.message}")
            }
        }
    }
}
