package com.android.tripbook.datamining.data.repository

import com.android.tripbook.datamining.data.database.entities.Destination
import com.android.tripbook.datamining.data.database.entities.TravelPattern
import com.android.tripbook.datamining.data.database.entities.UserPreference
import java.util.Calendar
import java.util.Date

/**
 * Generates sample data for the data mining feature
 */

// Sample destinations for demonstration
fun getSampleDestinations(): List<Destination> {
    val now = Date()
    return listOf(
        Destination(
            name = "Zanzibar",
            country = "Tanzania",
            region = "East Africa",
            popularity = 92.5f,
            averageRating = 4.8f,
            visitCount = 15420,
            lastUpdated = now,
            latitude = -6.165917,
            longitude = 39.202641,
            imageUrl = "https://images.unsplash.com/photo-1586500036706-41963de24d8c",
            description = "Beautiful beaches with crystal clear waters, perfect for relaxation and water activities.",
            tags = "beach,island,relaxation,snorkeling,culture",
            seasonalityData = "{\"Jan\":85,\"Feb\":88,\"Mar\":90,\"Apr\":75,\"May\":65,\"Jun\":70,\"Jul\":85,\"Aug\":95,\"Sep\":90,\"Oct\":85,\"Nov\":80,\"Dec\":95}"
        ),
        Destination(
            name = "Serengeti National Park",
            country = "Tanzania",
            region = "East Africa",
            popularity = 95.0f,
            averageRating = 4.9f,
            visitCount = 18750,
            lastUpdated = now,
            latitude = -2.3333333,
            longitude = 34.8333333,
            imageUrl = "https://images.unsplash.com/photo-1516426122078-c23e76319801",
            description = "Home to the Great Migration, offering incredible wildlife viewing opportunities.",
            tags = "safari,wildlife,nature,photography,adventure",
            seasonalityData = "{\"Jan\":80,\"Feb\":85,\"Mar\":75,\"Apr\":65,\"May\":70,\"Jun\":90,\"Jul\":98,\"Aug\":99,\"Sep\":95,\"Oct\":85,\"Nov\":75,\"Dec\":80}"
        ),
        Destination(
            name = "Cape Town",
            country = "South Africa",
            region = "Southern Africa",
            popularity = 90.0f,
            averageRating = 4.7f,
            visitCount = 22340,
            lastUpdated = now,
            latitude = -33.9249,
            longitude = 18.4241,
            imageUrl = "https://images.unsplash.com/photo-1580060839134-75a5edca2e99",
            description = "Stunning coastal city with Table Mountain as backdrop, offering diverse experiences.",
            tags = "city,mountain,beach,wine,culture",
            seasonalityData = "{\"Jan\":95,\"Feb\":90,\"Mar\":85,\"Apr\":75,\"May\":65,\"Jun\":60,\"Jul\":65,\"Aug\":70,\"Sep\":80,\"Oct\":85,\"Nov\":90,\"Dec\":98}"
        ),
        Destination(
            name = "Marrakech",
            country = "Morocco",
            region = "North Africa",
            popularity = 88.0f,
            averageRating = 4.6f,
            visitCount = 19870,
            lastUpdated = now,
            latitude = 31.6295,
            longitude = -7.9811,
            imageUrl = "https://images.unsplash.com/photo-1597212618440-806262de4f6b",
            description = "Vibrant city with colorful markets, historic architecture, and rich cultural experiences.",
            tags = "culture,market,history,architecture,food",
            seasonalityData = "{\"Jan\":65,\"Feb\":70,\"Mar\":80,\"Apr\":85,\"May\":80,\"Jun\":75,\"Jul\":70,\"Aug\":75,\"Sep\":85,\"Oct\":90,\"Nov\":80,\"Dec\":75}"
        ),
        Destination(
            name = "Victoria Falls",
            country = "Zimbabwe/Zambia",
            region = "Southern Africa",
            popularity = 87.0f,
            averageRating = 4.8f,
            visitCount = 16540,
            lastUpdated = now,
            latitude = -17.9244,
            longitude = 25.8567,
            imageUrl = "https://images.unsplash.com/photo-1544735716-ea9ef4aec7cc",
            description = "One of the world's largest waterfalls, offering breathtaking views and adventure activities.",
            tags = "waterfall,nature,adventure,hiking,photography",
            seasonalityData = "{\"Jan\":80,\"Feb\":85,\"Mar\":90,\"Apr\":95,\"May\":85,\"Jun\":75,\"Jul\":70,\"Aug\":65,\"Sep\":70,\"Oct\":80,\"Nov\":85,\"Dec\":80}"
        ),
        Destination(
            name = "Cairo",
            country = "Egypt",
            region = "North Africa",
            popularity = 86.0f,
            averageRating = 4.5f,
            visitCount = 25430,
            lastUpdated = now,
            latitude = 30.0444,
            longitude = 31.2357,
            imageUrl = "https://images.unsplash.com/photo-1572252009286-268acec5ca0a",
            description = "Historic city home to the Pyramids and Sphinx, with rich ancient Egyptian heritage.",
            tags = "history,pyramids,culture,archaeology,museum",
            seasonalityData = "{\"Jan\":75,\"Feb\":80,\"Mar\":85,\"Apr\":80,\"May\":75,\"Jun\":70,\"Jul\":65,\"Aug\":70,\"Sep\":80,\"Oct\":85,\"Nov\":80,\"Dec\":85}"
        ),
        Destination(
            name = "Maasai Mara",
            country = "Kenya",
            region = "East Africa",
            popularity = 89.0f,
            averageRating = 4.8f,
            visitCount = 17650,
            lastUpdated = now,
            latitude = -1.5167,
            longitude = 35.1167,
            imageUrl = "https://images.unsplash.com/photo-1547970810-dc1eac37d174",
            description = "Famous wildlife reserve known for the Great Migration and exceptional safari experiences.",
            tags = "safari,wildlife,nature,photography,adventure",
            seasonalityData = "{\"Jan\":75,\"Feb\":70,\"Mar\":65,\"Apr\":60,\"May\":65,\"Jun\":75,\"Jul\":95,\"Aug\":98,\"Sep\":90,\"Oct\":80,\"Nov\":75,\"Dec\":80}"
        ),
        Destination(
            name = "Seychelles",
            country = "Seychelles",
            region = "East Africa",
            popularity = 85.0f,
            averageRating = 4.9f,
            visitCount = 14320,
            lastUpdated = now,
            latitude = -4.6796,
            longitude = 55.492,
            imageUrl = "https://images.unsplash.com/photo-1589979481223-deb893043163",
            description = "Pristine beaches and crystal-clear waters in this island paradise.",
            tags = "beach,island,luxury,honeymoon,snorkeling",
            seasonalityData = "{\"Jan\":90,\"Feb\":85,\"Mar\":80,\"Apr\":85,\"May\":80,\"Jun\":75,\"Jul\":80,\"Aug\":85,\"Sep\":80,\"Oct\":75,\"Nov\":85,\"Dec\":95}"
        )
    )
}

// Sample travel patterns for demonstration
fun getSampleTravelPatterns(): List<TravelPattern> {
    val calendar = Calendar.getInstance()
    val now = Date()
    
    // Set start date to beginning of current year
    calendar.time = now
    calendar.set(Calendar.MONTH, Calendar.JANUARY)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startDate = calendar.time
    
    // Set end date to end of current year
    calendar.set(Calendar.MONTH, Calendar.DECEMBER)
    calendar.set(Calendar.DAY_OF_MONTH, 31)
    val endDate = calendar.time
    
    return listOf(
        TravelPattern(
            userId = "global",
            patternType = "seasonal",
            patternName = "Summer Safari Peak",
            patternValue = 0.85f,
            patternData = "{\"months\":[\"July\",\"August\",\"September\"],\"destinations\":[\"Serengeti\",\"Maasai Mara\"],\"activities\":[\"wildlife viewing\",\"photography\"]}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.92f,
            sampleSize = 15420
        ),
        TravelPattern(
            userId = "global",
            patternType = "destination_correlation",
            patternName = "East Africa Circuit",
            patternValue = 0.78f,
            patternData = "{\"primary\":\"Serengeti\",\"secondary\":[\"Zanzibar\",\"Maasai Mara\"],\"averageDuration\":12}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.88f,
            sampleSize = 8750
        ),
        TravelPattern(
            userId = "global",
            patternType = "duration",
            patternName = "Beach Destination Duration",
            patternValue = 6.5f, // Average days
            patternData = "{\"destinations\":[\"Zanzibar\",\"Seychelles\"],\"range\":[4,10],\"mode\":7}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.85f,
            sampleSize = 12340
        ),
        TravelPattern(
            userId = "user123",
            patternType = "seasonal",
            patternName = "Winter Escape",
            patternValue = 0.75f,
            patternData = "{\"months\":[\"December\",\"January\"],\"destinations\":[\"Cape Town\",\"Zanzibar\"],\"activities\":[\"beach\",\"relaxation\"]}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.82f,
            sampleSize = 24
        ),
        TravelPattern(
            userId = "user123",
            patternType = "activity_preference",
            patternName = "Adventure Seeker",
            patternValue = 0.88f,
            patternData = "{\"activities\":[\"safari\",\"hiking\",\"water sports\"],\"intensity\":\"high\"}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.90f,
            sampleSize = 35
        )
    )
}

// Sample user preferences for demonstration
fun getSampleUserPreferences(): List<UserPreference> {
    val now = Date()
    return listOf(
        UserPreference(
            userId = "user123",
            preferenceType = "destination_type",
            preferenceValue = "beach",
            preferenceStrength = 0.85f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.95f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "destination_type",
            preferenceValue = "wildlife",
            preferenceStrength = 0.78f,
            lastUpdated = now,
            source = "implicit",
            confidence = 0.82f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "activity",
            preferenceValue = "safari",
            preferenceStrength = 0.92f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.95f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "accommodation",
            preferenceValue = "luxury",
            preferenceStrength = 0.65f,
            lastUpdated = now,
            source = "inferred",
            confidence = 0.75f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "travel_style",
            preferenceValue = "adventure",
            preferenceStrength = 0.88f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.90f
        )
    )
}
