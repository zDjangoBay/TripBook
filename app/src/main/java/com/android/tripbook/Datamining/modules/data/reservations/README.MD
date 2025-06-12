# Datamining - Reservations Module

<p>This document provides a comprehensive overview of the Reservations Module. This module is responsible for managing user travel and activity reservations, including their creation, retrieval, updates, status tracking, and filtering. It utilizes a caching layer with Redis for performance and persistent storage via MongoDB.</p>

## Table of Contents

1.  [Module Overview](#module-overview)
    * [Key Functionalities](#key-functionalities)
    * [Technologies Used](#technologies-used)
2.  [Data Models](#data-models)
    * [Reservation (`Reservation.kt`)](#reservationkt)
    * [ReservationStatus (`ReservationStatus.kt`)](#reservationstatuskt)
    * [ReservationFilter (`ReservationFilter.kt`)](#reservationfilterkt)
    * [CreateReservationRequest DTO](#createreservationrequest-dto)
    * [UpdateReservationRequest DTO](#updatereservationrequest-dto)
    * [UpdateReservationStatusRequest DTO](#updatereservationstatusrequest-dto)
3.  [Service Layer](#service-layer)
    * [ReservationsService (Interface)](#reservationsservice-interface)
    * [ReservationServiceImpl (Implementation)](#reservationserviceimpl-implementation)
        * [Dependencies](#dependencies)
        * [MongoDB Collection](#mongodb-collection)
        * [Caching Strategy](#caching-strategy)
        * [Method Descriptions (Conceptual Highlights)](#method-descriptions-conceptual-highlights)
4.  [API Routes](#api-routes)
5.  [Configuration and Setup (Briefly)](#configuration-and-setup-briefly)
6.  [Future Considerations and Improvements](#future-considerations-and-improvements)

---

## 1. Module Overview

The Reservations Module allows users to manage their bookings for various travel-related services such as accommodations, transport, and activities. It provides a structured way to store, track, and modify reservation details.

### Key Functionalities

* **Reservation Management (CRUD):** Enables creating new reservations, retrieving existing ones (individually or in lists), updating details, and deleting reservations.
* **User-Specific Data:** Reservations are tied to specific users, ensuring data privacy and personalized access.
* **Status Tracking:** Supports various reservation statuses like `PENDING`, `CONFIRMED`, `CANCELLED`, and `COMPLETED`.
* **Advanced Filtering:** Allows users to filter their reservations based on criteria such as status, destination, date ranges, price, and a general search query.
* **Caching:** Employs Redis for caching frequently accessed reservation data to improve response times.
* **Persistence:** Utilizes MongoDB for reliable and scalable storage of reservation information.

### Technologies Used

* **Kotlin:** Primary programming language.
* **Ktor:** Framework used for building the server and API routes.
* **MongoDB:** NoSQL database for persistent storage of reservation data.
* **Redis:** In-memory data store for caching reservation data.
* **Kotlinx Serialization:** For serializing and deserializing Kotlin objects to/from JSON.

## 2. Data Models

The module defines the structure for reservations, their statuses, filtering criteria, and request payloads.

### `Reservation.kt`

This data class is the core entity representing a user's reservation.

```kotlin

@Serializable
data class Reservation(
    val id: String = UUID.randomUUID().toString(), // Primary unique identifier
    val userId: String, // Identifier of the user who owns this reservation 
    val title: String, // A user-friendly title for the reservation
    val destination: String, // Primary destination of the reservation
    @Serializable(with = org.litote.kmongo.serialization.LocalDateTimeSerializer::class) // For KMongo
    val startDate: LocalDateTime, // Start date and time of the reservation
    @Serializable(with = org.litote.kmongo.serialization.LocalDateTimeSerializer::class) // For KMongo
    val endDate: LocalDateTime, // End date and time of the reservation
    val status: ReservationStatus, // Current status (e.g., PENDING, CONFIRMED)
    val imageUrl: String? = null, // Optional URL for an image related to the reservation
    val price: Double, // Cost of the reservation
    val currency: String = "USD", // Currency of the price
    val bookingReference: String, // Official booking reference number/code
    val notes: String? = null, // User's personal notes for the reservation
    val accommodationName: String? = null, // Name of the hotel, rental, etc.
    val accommodationAddress: String? = null, // Address of the accommodation
    val transportInfo: String? = null, // Details about transportation (flight number, train, etc.) but the teacher said mostly about Bus but this doesn;t exclude other transportations means 
    @Serializable(with = org.litote.kmongo.serialization.LocalDateTimeSerializer::class) // For KMongo
    val createdAt: LocalDateTime = LocalDateTime.now(), // Timestamp of creation 
    @Serializable(with = org.litote.kmongo.serialization.LocalDateTimeSerializer::class) // For KMongo
    var updatedAt: LocalDateTime = LocalDateTime.now()  // Timestamp of last update (ASSUMED ADDITION)
)

```

### `ReservationStatus.kt`

This data class defines the possible statuses for a reservation 

```kotlin
@Serializable
enum class ReservationStatus {
    CONFIRMED,
    PENDING,
    CANCELLED,
    COMPLETED;

    // Example helper function as provided
    fun getColorName(): String {
        return when (this) {
            CONFIRMED -> "Green"
            PENDING -> "Amber"
            CANCELLED -> "Red"
            COMPLETED -> "Blue"
        }
    }
}
```

### `ReservationFilter.kt`

This data class is used to specify filter criteria when querying for lists of reservations.


```kotlin
@Serializable
data class ReservationFilter(
    val status: Set<ReservationStatus>? = null, // Filter by one or more statuses
    val destination: String? = null, // Filter by destination (e.g., using a contains/regex match), i am not 100 % sure it works though , going to test it i guess 😩
    @Serializable(with = LocalDateTimeSerializer::class) // For internal use with KMongo
    val startDateFrom: LocalDateTime? = null, // Reservations starting on or after this date
    @Serializable(with = LocalDateTimeSerializer::class) // For internal use with KMongo
    val startDateTo: LocalDateTime? = null, // Reservations starting on or before this date
    val priceMin: Double? = null, // Minimum price
    val priceMax: Double? = null, // Maximum price
    val searchQuery: String? = null // A general text search query (e.g., for title, notes, booking ref)
)
```

### `CreateReservationRequest.kit(DTO)`

This is a Data Transfer Object that define the skeleton of the sent json for it to be sent in the databse 


```kotlin
@Serializable
data class CreateReservationRequest(
    val title: String,
    val destination: String,
    val startDate: String, // Expected as ISO-8601 String (e.g., "2025-12-31T18:00:00")
    val endDate: String,   // Expected as ISO-8601 String
    val status: ReservationStatus = ReservationStatus.PENDING, // Default status
    val imageUrl: String? = null,
    val price: Double,
    val currency: String? = "USD",
    val bookingReference: String,
    val notes: String? = null,
    val accommodationName: String? = null,
    val accommodationAddress: String? = null,
    val transportInfo: String? = null
    // I collected the user in a field , which is not correct , it must come from the payload of the JWToken
)
```

### `UpdateReservationRequest.kt` 

The DTO that allows us to update a reservation 

```kotlin
@Serializable
data class UpdateReservationRequest(
    val title: String? = null,
    val destination: String? = null,
    val startDate: String? = null, // Expected as ISO-8601 String meaning 8/17/2018 7:00:00 AM
    val endDate: String? = null,  
    val status: ReservationStatus? = null,
    val imageUrl: String? = null,
    val price: Double? = null,
    val currency: String? = null,
    val bookingReference: String? = null,
    val notes: String? = null,
    val accommodationName: String? = null,
    val accommodationAddress: String? = null,
    val transportInfo: String? = null
)
```

### `UpdateReservationStatusRequest.kt`

This Data Transfer Object  specifically for updating a reservation's status.

```kotlin
@Serializable
data class UpdateReservationStatusRequest(
    val status: ReservationStatus
)
```

## Service Layer 


### ReservationServiceImpl (Implementation)

This class provides the concrete implementation for `ReservationsService`. Based on the empty file provided, this describes a conceptual implementation consistent with previous modules.

#### Dependencies

* **CoroutineDatabase (MongoDB):** KMongo coroutine database instance.
* **Jedis (Redis):** Jedis client instance for Redis interaction.
* **Json (Kotlinx Serialization):** Used for JSON serialization and deserialization.

#### MongoDB Collection

Reservation data is stored in a MongoDB collection named "**reservations**."


## APi Routes 

The service methods are exposed through Ktor routes defined in `ReservationRoutes.kt`. User authentication is assumed to identify the `userId` for all operations.

| Route                          | Method | Description                                                              | Service Call (Conceptual)         |
| :----------------------------- | :----- | :----------------------------------------------------------------------- | :-------------------------------- |
| `/reservations`                | POST   | Creates a new reservation for the authenticated user.                      | `createReservation()`             |
| `/reservations`                | GET    | Retrieves all reservations for the authenticated user. Supports filtering by:\<br\>- `status` (CONFIRMED, PENDING, etc.)\<br\>- `destination` (text search)\<br\>- `startDateFrom`, `startDateTo` (ISO-8601 date strings)\<br\>- `priceMin`, `priceMax` (numeric)\<br\>- `searchQuery` (general text search)\<br\>- `page`, `size` (pagination) | `getUserReservations()`           |
| `/reservations/{id}`           | GET    | Retrieves a specific reservation by its ID (for the authenticated user).       | `getReservationById()`            |
| `/reservations/{id}`           | PUT    | Updates an existing reservation (for the authenticated user).                  | `updateReservation()`             |
| `/reservations/{id}/status`    | PATCH  | Updates the status of a specific reservation (for the authenticated user).   | `updateReservationStatus()`       |
| `/reservations/{id}`           | DELETE | Deletes a specific reservation (for the authenticated user).                   | `deleteReservation()`             |

