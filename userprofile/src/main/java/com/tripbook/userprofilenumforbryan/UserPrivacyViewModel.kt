package com.tripbook.userprofilenumforbryan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserPrivacyViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    // Expose visibility as StateFlow for Compose
    val visibility: StateFlow<Visibility> = dataStoreManager.visibilityFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Visibility.FRIENDS
    )

    fun updateVisibility(newVisibility: Visibility) {
        viewModelScope.launch {
            dataStoreManager.saveVisibility(newVisibility)
        }
    }
}

class UserPrivacyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStoreManager = DataStoreManager(context)
        @Suppress("UNCHECKED_CAST")
        return UserPrivacyViewModel(dataStoreManager) as T
    }
}
