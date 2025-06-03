package com.android.tripbook.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * DataStore provider for the application
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tripbook_preferences")

object DataStoreProvider {
    fun getDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
