// ServiceRepository.kt
package com.android.tripbook.data.repositories

import com.android.tripbook.data.models.Service


interface ServiceRepository {
    fun searchServices(query: String?): List<Service>
    fun getServiceById(id: String): Service?

}



