package com.android.tripbook

import kotlinx.coroutines.flow.Flow

class SouvenirRepository(private val souvenirDao: SouvenirDao) {
    val allSouvenirs: Flow<List<Souvenir>> = souvenirDao.getAllSouvenirs()

    suspend fun insert(souvenir: Souvenir) {
        souvenirDao.insert(souvenir)
    }

    suspend fun delete(souvenir: Souvenir) {
        souvenirDao.delete(souvenir)
    }
}