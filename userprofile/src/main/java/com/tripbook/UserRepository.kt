package com.yourpackage.userprofile.data.repository

import com.yourpackage.userprofile.data.models.*

class UserRepository {

    // Simulated user profile data (in a real app, this comes from a server or database)
    private val dummyUser = User(
        id = "U12345",
        username = "godwillazieh",
        fullName = "Azieh Godwill",
        email = "azieh@example.com",
        address = Address(
            street = "123 Innovation Blvd",
            city = "Yaoundé",
            country = "Cameroon",
            postalCode = "237"
        ),
        contactInfo = ContactInfo(
            phoneNumber = "+237678000111",
            email = "azieh@example.com"
        ),
        emergencyContact = EmergencyContact(
            name = "Jane Doe",
            relationship = "Sister",
            phone = "+237690123456"
        ),
        preferences = UserPreferences(
            prefersDarkMode = true,
            language = "en",
            notificationsEnabled = true
        ),
        profilePicture = ProfilePicture(
            url = "https://example.com/profile.jpg",
            lastUpdated = "2025-05-26T12:00:00Z"
        ),
        documents = listOf(
            Document(
                type = "Passport",
                number = "P123456789",
                expirationDate = "2030-01-01"
            )
        )
    )

    fun getUserProfile(): User {
        return dummyUser
    }

    fun updateUserProfile(updatedUser: User): Boolean {
        // In a real scenario, you would perform an API call or DB update
        println("User profile updated: $updatedUser")
        return true
    }
}
