package com.tripbook.controller

import com.tripbook.models.TripReview
import com.tripbook.service.TripReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reviews")
class TripReviewController(private val tripReviewService: TripReviewService) {

    @PostMapping
    fun createReview(@RequestBody review: TripReview): ResponseEntity<TripReview> {
        return ResponseEntity.ok(tripReviewService.createReview(review))
    }

    @GetMapping("/user/{userId}")
    fun getReviewsByUser(@PathVariable userId: Long): ResponseEntity<List<TripReview>> {
        return ResponseEntity.ok(tripReviewService.getReviewsByUser(userId))
    }

    @GetMapping("/agency/{agencyId}")
    fun getReviewsByAgency(@PathVariable agencyId: Long): ResponseEntity<List<TripReview>> {
        return ResponseEntity.ok(tripReviewService.getReviewsByAgency(agencyId))
    }

    @GetMapping("/destination/{destination}")
    fun getReviewsByDestination(@PathVariable destination: String): ResponseEntity<List<TripReview>> {
        return ResponseEntity.ok(tripReviewService.getReviewsByDestination(destination))
    }

    @GetMapping("/verified")
    fun getVerifiedReviews(): ResponseEntity<List<TripReview>> {
        return ResponseEntity.ok(tripReviewService.getVerifiedReviews())
    }

    @PutMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @RequestBody updatedReview: TripReview
    ): ResponseEntity<TripReview> {
        return ResponseEntity.ok(tripReviewService.updateReview(reviewId, updatedReview))
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(@PathVariable reviewId: Long): ResponseEntity<Void> {
        tripReviewService.deleteReview(reviewId)
        return ResponseEntity.noContent().build()
    }
} 