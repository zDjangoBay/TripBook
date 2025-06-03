package com.android.tripbook.data

import com.android.tripbook.model.Comment
import java.text.SimpleDateFormat
import java.util.*

object SampleComments {

    fun generateMockComments(reviewId: Int): List<Comment> {
        val random = Random(reviewId.toLong()) // Use reviewId as seed for consistency

        val sampleComments = listOf(
            "This mountain landscape is absolutely breathtaking! The rolling hills covered in morning mist create such a serene atmosphere. I spent hours just sitting here absorbing the tranquility and watching the sunrise paint the sky in brilliant oranges and pinks.",
            "The coastal views from this cliff are nothing short of spectacular! Watching the waves crash against the rocky shoreline while seabirds dance in the ocean breeze was mesmerizing. The sound of the ocean is so therapeutic and grounding.",
            "This forest trail leads to some of the most pristine wilderness I've ever experienced. The ancient trees tower overhead creating a natural cathedral, and the dappled sunlight filtering through the canopy creates magical moments throughout the hike.",
            "The sunset from this vantage point is legendary for good reason! As the golden hour approaches, the entire valley below is bathed in warm light, and the distant mountains create a stunning silhouette against the vibrant sky. Pure magic!",
            "This hidden waterfall is a true gem tucked away in the wilderness. The crystal-clear water cascades down moss-covered rocks into a pristine pool below. The negative ions from the falling water make you feel instantly refreshed and energized.",
            "The architectural details of this historic site tell stories of centuries past. Every stone has been carefully placed, and you can almost feel the history seeping through the weathered walls. The craftsmanship is absolutely remarkable.",
            "These hiking trails offer some of the most rewarding panoramic views I've encountered. The gradual ascent through diverse ecosystems - from dense forests to alpine meadows - makes every step worthwhile when you reach those breathtaking overlooks.",
            "The value here is incredible considering the pristine natural beauty you get access to. Clean facilities, well-maintained trails, and knowledgeable staff who genuinely care about conservation make this a responsible traveler's dream destination.",
            "Visiting during spring was absolutely perfect timing! The wildflowers were in full bloom, creating carpets of vibrant colors across the meadows. The mild temperatures and gentle breezes made every outdoor activity absolutely delightful.",
            "The local artisan market here is a cultural treasure trove! Each handmade piece tells a story of traditional craftsmanship passed down through generations. The authentic local crafts and warm hospitality make for unforgettable souvenir shopping.",
            "This glacial lake reflects the surrounding peaks like a perfect mirror on calm days. The crystal-clear water is so pure you can see straight to the bottom, and the contrast between the turquoise water and snow-capped mountains is simply stunning.",
            "The wildlife viewing opportunities here are unparalleled! We spotted eagles soaring overhead, deer grazing in meadows, and even caught glimpses of more elusive creatures. The biodiversity in this ecosystem is truly remarkable.",
            "This ancient oak grove feels like stepping into a fairy tale. Some of these majestic trees have been standing for hundreds of years, their gnarled branches creating natural sculptures against the sky. The sense of timelessness is overwhelming.",
            "The stargazing from this remote location is absolutely mind-blowing! With minimal light pollution, the Milky Way stretches across the entire sky in brilliant detail. Bring a blanket and prepare to be humbled by the vastness of the universe.",
            "This hidden canyon reveals geological wonders at every turn. The layered rock formations tell the story of millions of years of Earth's history, and the play of light and shadow throughout the day creates an ever-changing natural art gallery."
        )

        val sampleNames = listOf(
            "Sarah Johnson", "Mike Chen", "Emily Rodriguez", "David Kim",
            "Jessica Brown", "Alex Thompson", "Maria Garcia", "James Wilson",
            "Lisa Anderson", "Ryan Martinez", "Amanda Davis", "Chris Lee",
            "Rachel Taylor", "Jordan Parker", "Maya Patel", "Sam Mitchell"
        )

        val sampleImages = listOf(
            "https://picsum.photos/300/200?random=1",
            "https://picsum.photos/300/200?random=2",
            "https://picsum.photos/300/200?random=3",
            "https://picsum.photos/300/200?random=4",
            "https://picsum.photos/300/200?random=5",
            null, null, null // Some comments without images
        )

        val numberOfComments = random.nextInt(4) + 3 // 3-6 comments per review

        return (0 until numberOfComments).map { index ->
            val daysAgo = random.nextInt(29) + 1 // 1-29 days ago
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

            Comment(
                id = "${reviewId}_$index",
                text = sampleComments[random.nextInt(sampleComments.size)],
                imageUri = sampleImages[random.nextInt(sampleImages.size)],
                timestamp = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(calendar.time),
                authorName = sampleNames[random.nextInt(sampleNames.size)]
            )
        }.sortedByDescending { it.timestamp } // Most recent first
    }
}