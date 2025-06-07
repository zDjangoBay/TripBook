
package com.android.tripbook.companycatalog.data

object MockData {
    val companies = listOf(
        Company(
            id = "1",
            name = "Cameroon Safari Adventures",
            description = "Discover the wild beauty of Cameroon with our expert-guided safari tours. Experience the diverse wildlife in Waza National Park, including elephants, lions, and giraffes. We offer customized packages for individuals and groups.",
            category = "Safari & Wildlife",
            rating = 4.8f,
            totalRatings = 245,
            location = "Waza National Park",
            city = "Maroua",
            region = "Far North Region",
            phoneNumber = "+237 693 45 67 89",
            email = "info@cameroon-safari.cm",
            website = "www.cameroon-safari.cm",
            imageUrl = "https://example.com/safari1.jpg",
            imageUrls = listOf(
                "https://example.com/safari1.jpg",
                "https://example.com/safari2.jpg",
                "https://example.com/safari3.jpg"
            ),
            services = listOf("Game Drives", "Bird Watching", "Photography Tours", "Cultural Visits"),
            amenities = listOf("4WD Vehicles", "Professional Guides", "Camping Equipment", "Meals Included"),
            priceRange = "$$$ (150,000-300,000 FCFA)",
            isPremium = true,
            isVerified = true,
            isOpen = true,
            openingHours = "6:00 AM - 6:00 PM",
            specialOffers = listOf("Early Bird 20% Discount", "Group Package Deals"),
            languages = listOf("French", "English", "Fulfulde"),
            establishedYear = 2010,
            likes = 1250,
            views = 15780,
            stars = 4.8f,
            followers = 3420,
            coordinates = Coordinates(11.0833, 14.6500)
        ),
        
        Company(
            id = "2",
            name = "Mount Cameroon Trekking",
            description = "Conquer the highest peak in West Africa! Our experienced guides will lead you through the challenging but rewarding trek up Mount Cameroon. Suitable for adventurous travelers seeking an unforgettable experience.",
            category = "Mountain Trekking",
            rating = 4.6f,
            totalRatings = 189,
            location = "Mount Cameroon",
            city = "Buea",
            region = "Southwest Region",
            phoneNumber = "+237 677 23 45 67",
            email = "climb@mountcameroon.cm",
            website = "www.mountcameroon-trek.cm",
            imageUrl = "https://example.com/mountain1.jpg",
            imageUrls = listOf(
                "https://example.com/mountain1.jpg",
                "https://example.com/mountain2.jpg",
                "https://example.com/mountain3.jpg"
            ),
            services = listOf("Guided Treks", "Equipment Rental", "Porter Services", "Photography"),
            amenities = listOf("Mountain Huts", "Safety Equipment", "First Aid", "Local Guides"),
            priceRange = "$$ (80,000-150,000 FCFA)",
            isPremium = false,
            isVerified = true,
            isOpen = true,
            openingHours = "5:00 AM - 7:00 PM",
            specialOffers = listOf("Student Discounts", "Multi-day Packages"),
            languages = listOf("English", "French", "Mokpe"),
            establishedYear = 2015,
            likes = 890,
            views = 12450,
            stars = 4.6f,
            followers = 2156,
            coordinates = Coordinates(4.2015, 9.1699)
        ),
        
        Company(
            id = "3",
            name = "Douala City Tours",
            description = "Explore the economic capital of Cameroon with our comprehensive city tours. Visit markets, museums, restaurants, and experience the vibrant nightlife of Douala. Perfect for business travelers and tourists alike.",
            category = "City Tours",
            rating = 4.3f,
            totalRatings = 156,
            location = "Central Douala",
            city = "Douala",
            region = "Littoral Region",
            phoneNumber = "+237 655 78 90 12",
            email = "tours@douala-city.cm",
            website = "www.douala-tours.cm",
            imageUrl = "https://example.com/douala1.jpg",
            imageUrls = listOf(
                "https://example.com/douala1.jpg",
                "https://example.com/douala2.jpg",
                "https://example.com/douala3.jpg"
            ),
            services = listOf("City Tours", "Airport Transfers", "Hotel Bookings", "Restaurant Reservations"),
            amenities = listOf("Air-conditioned Vehicles", "WiFi", "Refreshments", "Multilingual Guides"),
            priceRange = "$ (25,000-80,000 FCFA)",
            isPremium = false,
            isVerified = true,
            isOpen = true,
            openingHours = "7:00 AM - 9:00 PM",
            specialOffers = listOf("Half-day Tours", "Night Life Packages"),
            languages = listOf("French", "English", "Duala"),
            establishedYear = 2018,
            likes = 654,
            views = 8934,
            stars = 4.3f,
            followers = 1789,
            coordinates = Coordinates(4.0511, 9.7679)
        ),
        
        Company(
            id = "4",
            name = "Kribi Beach Resort Tours",
            description = "Relax and unwind at Cameroon's most beautiful beaches. Our beach tours include visits to Kribi's pristine shores, Chutes de la Lobe waterfalls, and fresh seafood experiences. Perfect for families and couples.",
            category = "Beach & Coastal",
            rating = 4.7f,
            totalRatings = 198,
            location = "Kribi Beach",
            city = "Kribi",
            region = "South Region",
            phoneNumber = "+237 698 34 56 78",
            email = "beach@kribi-tours.cm",
            website = "www.kribi-beach.cm",
            imageUrl = "https://example.com/kribi1.jpg",
            imageUrls = listOf(
                "https://example.com/kribi1.jpg",
                "https://example.com/kribi2.jpg",
                "https://example.com/kribi3.jpg"
            ),
            services = listOf("Beach Tours", "Water Sports", "Seafood Tours", "Waterfall Visits"),
            amenities = listOf("Beach Equipment", "Boat Rides", "Swimming Gear", "Picnic Areas"),
            priceRange = "$$ (60,000-120,000 FCFA)",
            isPremium = true,
            isVerified = true,
            isOpen = true,
            openingHours = "8:00 AM - 6:00 PM",
            specialOffers = listOf("Weekend Packages", "Honeymoon Specials"),
            languages = listOf("French", "English", "Bulu"),
            establishedYear = 2012,
            likes = 1456,
            views = 18923,
            stars = 4.7f,
            followers = 4321,
            coordinates = Coordinates(2.9404, 9.9073)
        ),
        
        Company(
            id = "5",
            name = "Yaoundé Cultural Heritage",
            description = "Immerse yourself in Cameroon's rich cultural heritage. Visit the National Museum, Mvog-Betsi Zoo, and traditional markets. Learn about local customs, art, and history in the capital city.",
            category = "Cultural Tours",
            rating = 4.4f,
            totalRatings = 134,
            location = "Centre Ville",
            city = "Yaoundé",
            region = "Centre Region",
            phoneNumber = "+237 670 12 34 56",
            email = "culture@yaounde-heritage.cm",
            website = "www.yaounde-culture.cm",
            imageUrl = "https://example.com/yaounde1.jpg",
            imageUrls = listOf(
                "https://example.com/yaounde1.jpg",
                "https://example.com/yaounde2.jpg",
                "https://example.com/yaounde3.jpg"
            ),
            services = listOf("Museum Tours", "Cultural Shows", "Art Workshops", "Historical Walks"),
            amenities = listOf("Cultural Guides", "Transportation", "Entry Tickets", "Souvenirs"),
            priceRange = "$ (30,000-75,000 FCFA)",
            isPremium = false,
            isVerified = true,
            isOpen = true,
            openingHours = "9:00 AM - 5:00 PM",
            specialOffers = listOf("Student Groups", "Cultural Packages"),
            languages = listOf("French", "English", "Ewondo"),
            establishedYear = 2016,
            likes = 567,
            views = 7854,
            stars = 4.4f,
            followers = 1234,
            coordinates = Coordinates(3.8480, 11.5021)
        )
    )
    
    val reviews = listOf(
        Review(
            id = "r1",
            userName = "Marie Tchounteu",
            userAvatar = "https://example.com/avatar1.jpg",
            rating = 5.0f,
            comment = "Amazing safari experience! Saw elephants, lions, and so many birds. Guide was very knowledgeable.",
            date = "2024-01-15",
            images = listOf("https://example.com/review1.jpg")
        ),
        Review(
            id = "r2",
            userName = "Jean-Paul Mbarga",
            userAvatar = "https://example.com/avatar2.jpg",
            rating = 4.5f,
            comment = "Great mountain trek! Challenging but worth it. Beautiful views from the top.",
            date = "2024-01-10"
        ),
        Review(
            id = "r3",
            userName = "Sarah Johnson",
            userAvatar = "https://example.com/avatar3.jpg",
            rating = 4.0f,
            comment = "Nice city tour of Douala. Good guide and comfortable transport.",
            date = "2024-01-08"
        )
    )
}
