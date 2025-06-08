package com.android.tripbook.service

import com.android.tripbook.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Service responsible for calculating trip milestones and generating notifications
 */
class MilestoneCalculatorService {
    
    /**
     * Calculate all milestones for a given trip
     */
    fun calculateMilestonesForTrip(
        trip: Trip, 
        preferences: NotificationPreferences
    ): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        val now = LocalDateTime.now()
        
        // Trip starting reminders
        if (preferences.enableTripStartReminders) {
            milestones.addAll(createTripStartingMilestones(trip, preferences))
        }
        
        // Activity reminders
        if (preferences.enableActivityReminders) {
            milestones.addAll(createActivityMilestones(trip, preferences))
        }
        
        // Budget reminders
        if (preferences.enableBudgetReminders) {
            milestones.addAll(createBudgetMilestones(trip, preferences))
        }
        
        // Packing reminders
        if (preferences.enablePackingReminders) {
            milestones.addAll(createPackingMilestones(trip, preferences))
        }
        
        // Document reminders
        if (preferences.enableDocumentReminders) {
            milestones.addAll(createDocumentMilestones(trip, preferences))
        }
        
        // Trip completion milestones
        milestones.addAll(createTripCompletionMilestones(trip))
        
        // Filter out past milestones (except for active trips)
        return milestones.filter { milestone ->
            milestone.triggerDate.isAfter(now) || trip.status == TripStatus.ACTIVE
        }
    }
    
    /**
     * Create trip starting milestone notifications
     */
    private fun createTripStartingMilestones(
        trip: Trip, 
        preferences: NotificationPreferences
    ): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        val tripStartDateTime = trip.startDate.atTime(9, 0) // 9 AM on trip start date
        
        // Create reminders for specified days before trip
        preferences.tripStartReminderDays.forEach { daysBefore ->
            val reminderDateTime = tripStartDateTime.minusDays(daysBefore.toLong())
            
            milestones.add(
                TripMilestone(
                    id = "${trip.id}_start_${daysBefore}d",
                    tripId = trip.id,
                    type = NotificationType.TRIP_STARTING_SOON,
                    triggerDate = reminderDateTime
                )
            )
        }
        
        // Trip starting today notification
        milestones.add(
            TripMilestone(
                id = "${trip.id}_start_today",
                tripId = trip.id,
                type = NotificationType.TRIP_STARTING_TODAY,
                triggerDate = tripStartDateTime
            )
        )
        
        return milestones
    }
    
    /**
     * Create activity reminder milestones
     */
    private fun createActivityMilestones(
        trip: Trip, 
        preferences: NotificationPreferences
    ): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        
        // Only create activity reminders for planned trips with no activities
        if (trip.status == TripStatus.PLANNED && trip.activities.isEmpty()) {
            val reminderDateTime = LocalDateTime.now().plusDays(preferences.activityReminderDays.toLong())
            
            milestones.add(
                TripMilestone(
                    id = "${trip.id}_activities",
                    tripId = trip.id,
                    type = NotificationType.ADD_ACTIVITIES,
                    triggerDate = reminderDateTime
                )
            )
        }
        
        return milestones
    }
    
    /**
     * Create budget reminder milestones
     */
    private fun createBudgetMilestones(
        trip: Trip, 
        preferences: NotificationPreferences
    ): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        
        // Budget reminder 1 week before trip
        val budgetReminderDateTime = trip.startDate.atTime(10, 0).minusDays(7)
        
        milestones.add(
            TripMilestone(
                id = "${trip.id}_budget",
                tripId = trip.id,
                type = NotificationType.BUDGET_REMINDER,
                triggerDate = budgetReminderDateTime
            )
        )
        
        return milestones
    }
    
    /**
     * Create packing reminder milestones
     */
    private fun createPackingMilestones(
        trip: Trip, 
        preferences: NotificationPreferences
    ): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        
        // Packing reminder 3 days before trip
        val packingReminderDateTime = trip.startDate.atTime(18, 0).minusDays(3)
        
        milestones.add(
            TripMilestone(
                id = "${trip.id}_packing",
                tripId = trip.id,
                type = NotificationType.PACKING_REMINDER,
                triggerDate = packingReminderDateTime
            )
        )
        
        return milestones
    }
    
    /**
     * Create document reminder milestones
     */
    private fun createDocumentMilestones(
        trip: Trip, 
        preferences: NotificationPreferences
    ): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        
        // Document reminder 2 weeks before trip
        val documentReminderDateTime = trip.startDate.atTime(12, 0).minusDays(14)
        
        milestones.add(
            TripMilestone(
                id = "${trip.id}_documents",
                tripId = trip.id,
                type = NotificationType.DOCUMENT_REMINDER,
                triggerDate = documentReminderDateTime
            )
        )
        
        return milestones
    }
    
    /**
     * Create trip completion milestones
     */
    private fun createTripCompletionMilestones(trip: Trip): List<TripMilestone> {
        val milestones = mutableListOf<TripMilestone>()
        
        // Trip ending soon (1 day before end)
        val endingSoonDateTime = trip.endDate.atTime(20, 0).minusDays(1)
        milestones.add(
            TripMilestone(
                id = "${trip.id}_ending_soon",
                tripId = trip.id,
                type = NotificationType.TRIP_ENDING_SOON,
                triggerDate = endingSoonDateTime
            )
        )
        
        // Trip completed (1 day after end)
        val completedDateTime = trip.endDate.atTime(10, 0).plusDays(1)
        milestones.add(
            TripMilestone(
                id = "${trip.id}_completed",
                tripId = trip.id,
                type = NotificationType.TRIP_COMPLETED,
                triggerDate = completedDateTime
            )
        )
        
        return milestones
    }
    
    /**
     * Generate notification content for a milestone
     */
    fun generateNotificationForMilestone(
        milestone: TripMilestone, 
        trip: Trip
    ): TripNotification {
        val (title, message, priority) = when (milestone.type) {
            NotificationType.TRIP_STARTING_SOON -> {
                val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), trip.startDate)
                Triple(
                    "Trip Starting Soon! 🌍",
                    "Your trip to ${trip.destination} starts in $daysUntil day${if (daysUntil != 1L) "s" else ""}! Get ready for an amazing adventure.",
                    NotificationPriority.HIGH
                )
            }
            
            NotificationType.TRIP_STARTING_TODAY -> {
                Triple(
                    "Trip Starts Today! 🎉",
                    "Your trip to ${trip.destination} starts today! Have an amazing time and safe travels!",
                    NotificationPriority.URGENT
                )
            }
            
            NotificationType.ADD_ACTIVITIES -> {
                Triple(
                    "Plan Your Adventure 📝",
                    "Add activities to your ${trip.name} itinerary to make the most of your trip to ${trip.destination}!",
                    NotificationPriority.NORMAL
                )
            }
            
            NotificationType.BUDGET_REMINDER -> {
                Triple(
                    "Budget Check 💰",
                    "Don't forget to track your expenses for ${trip.name}. Your budget is $${trip.budget}.",
                    NotificationPriority.NORMAL
                )
            }
            
            NotificationType.PACKING_REMINDER -> {
                Triple(
                    "Time to Pack! 🧳",
                    "Start packing for your trip to ${trip.destination}. Don't forget the essentials!",
                    NotificationPriority.HIGH
                )
            }
            
            NotificationType.DOCUMENT_REMINDER -> {
                Triple(
                    "Check Your Documents 📄",
                    "Make sure your passport, visa, and other travel documents are ready for ${trip.destination}.",
                    NotificationPriority.HIGH
                )
            }
            
            NotificationType.TRIP_ENDING_SOON -> {
                Triple(
                    "Trip Ending Soon 😢",
                    "Your amazing trip to ${trip.destination} ends tomorrow. Make the most of your last day!",
                    NotificationPriority.NORMAL
                )
            }
            
            NotificationType.TRIP_COMPLETED -> {
                Triple(
                    "Welcome Back! ✈️",
                    "How was your trip to ${trip.destination}? Share your experience and rate your journey!",
                    NotificationPriority.NORMAL
                )
            }
        }
        
        return TripNotification(
            id = "${milestone.id}_notification",
            tripId = trip.id,
            type = milestone.type,
            title = title,
            message = message,
            scheduledTime = milestone.triggerDate,
            priority = priority,
            actionUrl = "tripbook://trip/${trip.id}",
            metadata = mapOf(
                "trip_name" to trip.name,
                "destination" to trip.destination,
                "trip_type" to trip.type
            )
        )
    }
}
