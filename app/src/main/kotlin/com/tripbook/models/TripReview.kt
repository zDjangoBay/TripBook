package com.tripbook.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "trip_reviews")
data class TripReview(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val destination: String,

    @Column(nullable = false)
    val rating: Int, // 1-5 stars

    @Column(length = 1000)
    val reviewText: String,

    @Column(nullable = false)
    val tripDate: LocalDateTime,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val isVerified: Boolean = false,

    @ElementCollection
    val photos: MutableList<String> = mutableListOf(),

    @Column(nullable = false)
    val safetyRating: Int, // 1-5 stars

    @Column(nullable = false)
    val valueForMoneyRating: Int, // 1-5 stars

    @Column(nullable = false)
    val experienceRating: Int, // 1-5 stars

    @ManyToOne
    @JoinColumn(name = "agency_id")
    val travelAgency: TravelAgency? = null
)

@Entity
@Table(name = "travel_agencies")
data class TravelAgency(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val location: String,

    @Column(nullable = false)
    val contactInfo: String,

    @Column(nullable = false)
    val averageRating: Double = 0.0,

    @Column(nullable = false)
    val totalReviews: Int = 0,

    @Column(nullable = false)
    val isVerified: Boolean = false,

    @OneToMany(mappedBy = "travelAgency")
    val reviews: MutableList<TripReview> = mutableListOf()
) 