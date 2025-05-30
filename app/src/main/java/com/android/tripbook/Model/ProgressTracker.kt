//data class ProgressTracker(
//    var currentStep: String = "Start",
//    var hasSelectedTransport: Boolean = false,
//    var hasSelectedHotel: Boolean = false,
//    var hasSelectedActivities: Boolean = false,
//    var isPaymentComplete: Boolean = false
//) {
//    fun toMap(): Map<String, Any> {
//        return mapOf(
//            "currentStep" to currentStep,
//            "hasSelectedTransport" to hasSelectedTransport,
//            "hasSelectedHotel" to hasSelectedHotel,
//            "hasSelectedActivities" to hasSelectedActivities,
//            "isPaymentComplete" to isPaymentComplete
//        )
//    }
//}
//
//fun main() {
//    val tracker = ProgressTracker()
//    tracker.currentStep = "TransportSelection"
//    tracker.hasSelectedTransport = true
//
//    println("Current progress: ${tracker.toMap()}")
//}

package com.android.tripbook.utils

data class ProgressTracker(
    var currentStep: String = "Start",
    var hasSelectedTransport: Boolean = false,
    var hasSelectedHotel: Boolean = false,
    var hasSelectedActivities: Boolean = false,
    var isPaymentComplete: Boolean = false,
    var isAuthenticated: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "currentStep" to currentStep,
            "hasSelectedTransport" to hasSelectedTransport,
            "hasSelectedHotel" to hasSelectedHotel,
            "hasSelectedActivities" to hasSelectedActivities,
            "isPaymentComplete" to isPaymentComplete,
            "isAuthenticated" to isAuthenticated
        )
    }

    fun resetProgress() {
        currentStep = "Start"
        hasSelectedTransport = false
        hasSelectedHotel = false
        hasSelectedActivities = false
        isPaymentComplete = false
        isAuthenticated = false
    }
}

// Singleton instance to be reused across the app
object UserProgress {
    val tracker = ProgressTracker()
}
