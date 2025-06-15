package com.tripbook.userprofileTalaDaryll.database

import kotlinx.coroutines.flow.Flow
import com.tripbook.userprofileTalaDaryll.models.Souvenir

class SouvenirRepository(private val souvenirDao: SouvenirDao) {
    val allSouvenirs: Flow<List<Souvenir>> = souvenirDao.getAllSouvenirs()

    suspend fun insert(souvenir: Souvenir) {
        souvenirDao.insert(souvenir)
    }

    suspend fun delete(souvenir: Souvenir) {
        souvenirDao.delete(souvenir)
    }
}