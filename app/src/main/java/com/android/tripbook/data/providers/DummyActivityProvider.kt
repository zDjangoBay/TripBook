package com.android.tripbook.data.providers

import com.android.tripbook.data.models.ActivityCategory
import com.android.tripbook.data.models.ActivityOption

/**
 * Provides dummy activity data for the application
 */
object DummyActivityProvider {
    
    fun getActivities(): List<ActivityOption> = listOf(
        ActivityOption(
            id = "activity_1",
            name = "Museum Tour",
            description = "Explore the rich history and culture at the National Museum",
            price = 25.0,
            duration = "2 hours",
            category = ActivityCategory.MUSEUM,
            imageUrl = "https://images.unsplash.com/photo-1554907984-15263bfd63bd"
        ),
        ActivityOption(
            id = "activity_2",
            name = "Boat Ride Safari",
            description = "Scenic boat ride along the river to spot hippos and crocodiles",
            price = 45.0,
            duration = "3 hours",
            category = ActivityCategory.OUTDOOR,
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5"
        ),
        ActivityOption(
            id = "activity_3",
            name = "Traditional Cooking Class",
            description = "Learn to cook authentic local dishes with a professional chef",
            price = 35.0,
            duration = "4 hours",
            category = ActivityCategory.FOOD,
            imageUrl = "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136"
        ),
        ActivityOption(
            id = "activity_4",
            name = "Cultural Dance Show",
            description = "Experience traditional dances and music performances",
            price = 20.0,
            duration = "1.5 hours",
            category = ActivityCategory.ENTERTAINMENT,
            imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"
        ),
        ActivityOption(
            id = "activity_5",
            name = "Village Cultural Tour",
            description = "Visit local villages and learn about traditional ways of life",
            price = 40.0,
            duration = "5 hours",
            category = ActivityCategory.CULTURAL,
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96"
        ),
        ActivityOption(
            id = "activity_6",
            name = "Hot Air Balloon Safari",
            description = "Breathtaking aerial views of the landscape and wildlife",
            price = 150.0,
            duration = "3 hours",
            category = ActivityCategory.ADVENTURE,
            imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4"
        ),
        ActivityOption(
            id = "activity_7",
            name = "Nature Walking Safari",
            description = "Guided walking tour through the wilderness",
            price = 30.0,
            duration = "2.5 hours",
            category = ActivityCategory.OUTDOOR,
            imageUrl = "https://images.unsplash.com/photo-1516026672322-bc52d61a55d5"
        ),
        ActivityOption(
            id = "activity_8",
            name = "Art Gallery Visit",
            description = "Discover contemporary African art and meet local artists",
            price = 15.0,
            duration = "1.5 hours",
            category = ActivityCategory.CULTURAL,
            imageUrl = "https://images.unsplash.com/photo-1541961017774-22349e4a1262"
        ),
        ActivityOption(
            id = "activity_9",
            name = "Sunset Game Drive",
            description = "Evening game drive to spot nocturnal animals",
            price = 60.0,
            duration = "4 hours",
            category = ActivityCategory.ADVENTURE,
            imageUrl = "https://images.unsplash.com/photo-1547036967-23d11aacaee0"
        ),
        ActivityOption(
            id = "activity_10",
            name = "Local Market Tour",
            description = "Explore bustling local markets and taste street food",
            price = 20.0,
            duration = "2 hours",
            category = ActivityCategory.FOOD,
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96"
        )
    )
    
    fun getActivityById(id: String): ActivityOption? = getActivities().find { it.id == id }
    
    fun getActivitiesByCategory(category: ActivityCategory): List<ActivityOption> {
        return getActivities().filter { it.category == category }
    }
    
    fun getActivitiesByPriceRange(minPrice: Double, maxPrice: Double): List<ActivityOption> {
        return getActivities().filter { it.price in minPrice..maxPrice }
    }
}
