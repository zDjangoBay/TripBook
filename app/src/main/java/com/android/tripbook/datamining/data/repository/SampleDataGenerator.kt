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
    val destinations = mutableListOf(
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
        ),

        // Cameroon Destinations - Adamawa Region
        Destination(
            name = "Vina Waterfalls",
            country = "Cameroon",
            region = "Adamawa Region",
            popularity = 82.0f,
            averageRating = 4.5f,
            visitCount = 8750,
            lastUpdated = now,
            latitude = 7.2372,
            longitude = 13.5803,
            imageUrl = "https://images.unsplash.com/photo-1564982752979-3f7c5f4a8b3b",
            description = "Spectacular waterfalls surrounded by lush vegetation in the Adamawa Region.",
            tags = "waterfall,nature,hiking,photography,adventure",
            seasonalityData = "{\"Jan\":70,\"Feb\":65,\"Mar\":60,\"Apr\":75,\"May\":85,\"Jun\":90,\"Jul\":85,\"Aug\":80,\"Sep\":85,\"Oct\":80,\"Nov\":75,\"Dec\":70}"
        ),

        // Cameroon Destinations - Centre Region
        Destination(
            name = "Mefou National Park",
            country = "Cameroon",
            region = "Centre Region",
            popularity = 84.0f,
            averageRating = 4.6f,
            visitCount = 12450,
            lastUpdated = now,
            latitude = 3.8667,
            longitude = 11.5167,
            imageUrl = "https://images.unsplash.com/photo-1584844115436-473887b1e327",
            description = "Home to the Mefou Primate Sanctuary with over 300 rescued primates including chimps and gorillas.",
            tags = "wildlife,primates,conservation,nature,sanctuary",
            seasonalityData = "{\"Jan\":75,\"Feb\":70,\"Mar\":65,\"Apr\":70,\"May\":80,\"Jun\":85,\"Jul\":80,\"Aug\":75,\"Sep\":70,\"Oct\":75,\"Nov\":80,\"Dec\":85}"
        ),

        // Cameroon Destinations - East Region
        Destination(
            name = "Dja Faunal Reserve",
            country = "Cameroon",
            region = "East Region",
            popularity = 86.0f,
            averageRating = 4.7f,
            visitCount = 9870,
            lastUpdated = now,
            latitude = 3.1667,
            longitude = 13.0000,
            imageUrl = "https://images.unsplash.com/photo-1552083974-186346191183",
            description = "UNESCO World Heritage site with pristine rainforest and exceptional biodiversity.",
            tags = "rainforest,wildlife,unesco,conservation,biodiversity",
            seasonalityData = "{\"Jan\":70,\"Feb\":65,\"Mar\":60,\"Apr\":65,\"May\":75,\"Jun\":85,\"Jul\":90,\"Aug\":85,\"Sep\":80,\"Oct\":75,\"Nov\":70,\"Dec\":75}"
        ),

        // Cameroon Destinations - Far North Region
        Destination(
            name = "Waza National Park",
            country = "Cameroon",
            region = "Far North Region",
            popularity = 88.0f,
            averageRating = 4.8f,
            visitCount = 14560,
            lastUpdated = now,
            latitude = 11.0000,
            longitude = 14.7500,
            imageUrl = "https://images.unsplash.com/photo-1549366021-9f761d450615",
            description = "Most visited national park in Cameroon with lions, elephants, giraffes and diverse wildlife.",
            tags = "safari,wildlife,nature,photography,adventure",
            seasonalityData = "{\"Jan\":85,\"Feb\":80,\"Mar\":75,\"Apr\":65,\"May\":60,\"Jun\":65,\"Jul\":70,\"Aug\":65,\"Sep\":70,\"Oct\":80,\"Nov\":85,\"Dec\":90}"
        ),

        // Cameroon Destinations - Littoral Region
        Destination(
            name = "Ekom-Nkam Waterfalls",
            country = "Cameroon",
            region = "Littoral Region",
            popularity = 85.0f,
            averageRating = 4.7f,
            visitCount = 11230,
            lastUpdated = now,
            latitude = 4.5833,
            longitude = 9.8833,
            imageUrl = "https://images.unsplash.com/photo-1564982752979-3f7c5f4a8b3b",
            description = "Impressive waterfalls featured in Tarzan films, surrounded by lush rainforest.",
            tags = "waterfall,nature,film,photography,adventure",
            seasonalityData = "{\"Jan\":65,\"Feb\":60,\"Mar\":65,\"Apr\":75,\"May\":85,\"Jun\":90,\"Jul\":85,\"Aug\":80,\"Sep\":75,\"Oct\":70,\"Nov\":65,\"Dec\":70}"
        ),

        // Cameroon Destinations - North Region
        Destination(
            name = "Bénoué National Park",
            country = "Cameroon",
            region = "North Region",
            popularity = 83.0f,
            averageRating = 4.5f,
            visitCount = 8950,
            lastUpdated = now,
            latitude = 8.3333,
            longitude = 13.8333,
            imageUrl = "https://images.unsplash.com/photo-1549366021-9f761d450615",
            description = "Wildlife reserve with diverse ecosystems and abundant wildlife including hippos and antelopes.",
            tags = "safari,wildlife,nature,river,adventure",
            seasonalityData = "{\"Jan\":80,\"Feb\":75,\"Mar\":70,\"Apr\":65,\"May\":60,\"Jun\":65,\"Jul\":70,\"Aug\":75,\"Sep\":80,\"Oct\":85,\"Nov\":80,\"Dec\":85}"
        ),

        // Cameroon Destinations - Northwest Region
        Destination(
            name = "Bafut Palace",
            country = "Cameroon",
            region = "Northwest Region",
            popularity = 87.0f,
            averageRating = 4.6f,
            visitCount = 13450,
            lastUpdated = now,
            latitude = 6.0833,
            longitude = 10.1333,
            imageUrl = "https://images.unsplash.com/photo-1566419808810-658178380987",
            description = "UNESCO World Heritage site, a traditional royal residence with rich cultural significance.",
            tags = "culture,history,architecture,unesco,heritage",
            seasonalityData = "{\"Jan\":75,\"Feb\":70,\"Mar\":65,\"Apr\":70,\"May\":80,\"Jun\":85,\"Jul\":80,\"Aug\":75,\"Sep\":70,\"Oct\":75,\"Nov\":80,\"Dec\":85}"
        ),

        // Cameroon Destinations - South Region
        Destination(
            name = "Kribi Beach",
            country = "Cameroon",
            region = "South Region",
            popularity = 89.0f,
            averageRating = 4.8f,
            visitCount = 16780,
            lastUpdated = now,
            latitude = 2.9333,
            longitude = 9.9167,
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
            description = "Beautiful white sand beaches along the Atlantic with fresh seafood and relaxing atmosphere.",
            tags = "beach,ocean,seafood,relaxation,swimming",
            seasonalityData = "{\"Jan\":85,\"Feb\":80,\"Mar\":75,\"Apr\":70,\"May\":75,\"Jun\":80,\"Jul\":85,\"Aug\":90,\"Sep\":85,\"Oct\":80,\"Nov\":85,\"Dec\":90}"
        ),

        // Cameroon Destinations - Southwest Region
        Destination(
            name = "Mount Cameroon",
            country = "Cameroon",
            region = "Southwest Region",
            popularity = 91.0f,
            averageRating = 4.9f,
            visitCount = 18950,
            lastUpdated = now,
            latitude = 4.2167,
            longitude = 9.1700,
            imageUrl = "https://images.unsplash.com/photo-1454496522488-7a8e488e8606",
            description = "Highest mountain in West and Central Africa (4,095m) with volcanic landscapes and diverse ecosystems.",
            tags = "mountain,hiking,volcano,adventure,nature",
            seasonalityData = "{\"Jan\":80,\"Feb\":75,\"Mar\":70,\"Apr\":65,\"May\":70,\"Jun\":75,\"Jul\":80,\"Aug\":85,\"Sep\":80,\"Oct\":75,\"Nov\":80,\"Dec\":85}"
        ),

        // Cameroon Destinations - West Region
        Destination(
            name = "Bafoussam Royal Palace",
            country = "Cameroon",
            region = "West Region",
            popularity = 84.0f,
            averageRating = 4.6f,
            visitCount = 12340,
            lastUpdated = now,
            latitude = 5.4667,
            longitude = 10.4167,
            imageUrl = "https://images.unsplash.com/photo-1566419808810-658178380987",
            description = "Historical palace with artifacts showcasing the rich cultural heritage of the region.",
            tags = "culture,history,architecture,museum,heritage",
            seasonalityData = "{\"Jan\":75,\"Feb\":70,\"Mar\":65,\"Apr\":70,\"May\":75,\"Jun\":80,\"Jul\":85,\"Aug\":80,\"Sep\":75,\"Oct\":70,\"Nov\":75,\"Dec\":80}"
        )
    )
    return destinations
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
        // Cameroon travel patterns
        TravelPattern(
            userId = "global",
            patternType = "seasonal",
            patternName = "Cameroon Dry Season Peak",
            patternValue = 0.82f,
            patternData = "{\"months\":[\"November\",\"December\",\"January\",\"February\"],\"destinations\":[\"Mount Cameroon\",\"Waza National Park\"],\"activities\":[\"hiking\",\"safari\",\"cultural tours\"]}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.88f,
            sampleSize = 9850
        ),
        TravelPattern(
            userId = "global",
            patternType = "destination_correlation",
            patternName = "Cameroon Explorer Circuit",
            patternValue = 0.75f,
            patternData = "{\"primary\":\"Mount Cameroon\",\"secondary\":[\"Kribi Beach\",\"Waza National Park\",\"Bafut Palace\"],\"averageDuration\":14}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.85f,
            sampleSize = 7250
        ),
        TravelPattern(
            userId = "global",
            patternType = "activity_preference",
            patternName = "Cameroon Cultural Experience",
            patternValue = 0.79f,
            patternData = "{\"activities\":[\"cultural tours\",\"historical sites\",\"traditional ceremonies\"],\"destinations\":[\"Bafut Palace\",\"Bafoussam Royal Palace\"],\"intensity\":\"medium\"}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.84f,
            sampleSize = 6540
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
        ),
        TravelPattern(
            userId = "user123",
            patternType = "destination_interest",
            patternName = "Cameroon Explorer",
            patternValue = 0.80f,
            patternData = "{\"destinations\":[\"Mount Cameroon\",\"Kribi Beach\"],\"activities\":[\"hiking\",\"beach\",\"cultural exploration\"],\"duration\":10}",
            startDate = startDate,
            endDate = endDate,
            confidence = 0.85f,
            sampleSize = 18
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
        ),

        // Cameroon-specific preferences
        UserPreference(
            userId = "user123",
            preferenceType = "country",
            preferenceValue = "Cameroon",
            preferenceStrength = 0.82f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.90f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "destination",
            preferenceValue = "Mount Cameroon",
            preferenceStrength = 0.88f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.95f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "destination",
            preferenceValue = "Kribi Beach",
            preferenceStrength = 0.75f,
            lastUpdated = now,
            source = "implicit",
            confidence = 0.80f
        ),
        UserPreference(
            userId = "user123",
            preferenceType = "activity",
            preferenceValue = "cultural tours",
            preferenceStrength = 0.70f,
            lastUpdated = now,
            source = "inferred",
            confidence = 0.75f
        ),
        UserPreference(
            userId = "user456",
            preferenceType = "country",
            preferenceValue = "Cameroon",
            preferenceStrength = 0.90f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.95f
        ),
        UserPreference(
            userId = "user456",
            preferenceType = "destination",
            preferenceValue = "Waza National Park",
            preferenceStrength = 0.85f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.90f
        ),
        UserPreference(
            userId = "user456",
            preferenceType = "activity",
            preferenceValue = "wildlife photography",
            preferenceStrength = 0.92f,
            lastUpdated = now,
            source = "explicit",
            confidence = 0.95f
        )
    )
}
