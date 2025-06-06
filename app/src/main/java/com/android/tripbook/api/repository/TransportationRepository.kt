package com.tripbook.api.repository

import com.tripbook.api.model.Transportation
import com.tripbook.api.model.TransportationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransportationRepository : JpaRepository<Transportation, String> {
    fun findByType(type: TransportationType): List<Transportation>
    fun findByStatus(status: TransportationStatus): List<Transportation>
} 