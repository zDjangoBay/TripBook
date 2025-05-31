package com.tripbook.repository

import com.tripbook.models.TripReview
import com.tripbook.models.TravelAgency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TripReviewRepository : JpaRepository<TripReview, Long> {
    fun findByUserId(userId: Long): List<TripReview>
    fun findByTravelAgencyId(agencyId: Long): List<TripReview>
    fun findByDestination(destination: String): List<TripReview>
    
    @Query("SELECT AVG(r.rating) FROM TripReview r WHERE r.travelAgency.id = ?1")
    fun getAverageRatingForAgency(agencyId: Long): Double?
    
    @Query("SELECT COUNT(r) FROM TripReview r WHERE r.travelAgency.id = ?1")
    fun getTotalReviewsForAgency(agencyId: Long): Long
    
    fun findByIsVerifiedTrue(): List<TripReview>
}

@Repository
interface TravelAgencyRepository : JpaRepository<TravelAgency, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<TravelAgency>
    fun findByLocation(location: String): List<TravelAgency>
    fun findByIsVerifiedTrue(): List<TravelAgency>
} 