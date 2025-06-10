
package com.android.tripbook.Abdoukarimuakande.data

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
            likes = 892,
            views = 12456,
            stars = 4.8f,
            followers = 2341,
            coordinates = Coordinates(10.591, 14.273)
        ),
        Company(
            id = "2",
            name = "Mount Cameroon Expeditions",
            description = "Conquer West Africa's highest peak with our experienced mountain guides. We provide all equipment and safety gear for your climbing adventure on Mount Cameroon.",
            category = "Mountain Climbing",
            rating = 4.6f,
            totalRatings = 189,
            location = "Mount Cameroon",
            city = "Buea",
            region = "Southwest Region",
            phoneNumber = "+237 675 23 45 67",
            email = "climb@mountcameroon.cm",
            website = "www.mountcameroon-expeditions.cm",
            imageUrl = "https://example.com/mountain1.jpg",
            imageUrls = listOf(
                "https://example.com/mountain1.jpg",
                "https://example.com/mountain2.jpg",
                "https://example.com/mountain3.jpg"
            ),
            services = listOf("Guided Climbing", "Equipment Rental", "Training Sessions", "Emergency Rescue"),
            amenities = listOf("Professional Guides", "Safety Equipment", "First Aid", "Accommodation"),
            priceRange = "$$ (80,000-200,000 FCFA)",
            isPremium = true,
            isVerified = true,
            isOpen = true,
            openingHours = "5:00 AM - 7:00 PM",
            specialOffers = listOf("Weekend Packages", "Student Discounts"),
            languages = listOf("English", "French", "Duala"),
            establishedYear = 2015,
            likes = 734,
            views = 9876,
            stars = 4.6f,
            followers = 1987,
            coordinates = Coordinates(4.2026, 9.1709)
        ),
        Company(
            id = "3",
            name = "Kribi Beach Resort",
            description = "Relax at Cameroon's most beautiful coastal destination. Our resort offers luxury accommodation, water sports, and cultural experiences along the pristine beaches of Kribi.",
            category = "Beach Resort",
            rating = 4.5f,
            totalRatings = 567,
            location = "Kribi Beach",
            city = "Kribi",
            region = "South Region",
            phoneNumber = "+237 694 78 90 12",
            email = "info@kribibeach.cm",
            website = "www.kribibeachresort.cm",
            imageUrl = "https://example.com/beach1.jpg",
            imageUrls = listOf(
                "https://example.com/beach1.jpg",
                "https://example.com/beach2.jpg",
                "https://example.com/beach3.jpg"
            ),
            services = listOf("Beach Activities", "Water Sports", "Spa Services", "Cultural Tours"),
            amenities = listOf("Swimming Pool", "Restaurant", "Spa", "Water Sports Equipment"),
            priceRange = "$$$ (120,000-400,000 FCFA)",
            isPremium = true,
            isVerified = true,
            isOpen = true,
            openingHours = "24/7",
            specialOffers = listOf("Honeymoon Package", "Family Deals"),
            languages = listOf("French", "English", "Bulu"),
            establishedYear = 2008,
            likes = 1245,
            views = 18765,
            stars = 4.5f,
            followers = 3456,
            coordinates = Coordinates(2.9343, 9.9073)
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
            rating = 4.0f,
            comment = "Great mountain climbing experience. Challenging but rewarding. Guides were professional.",
            date = "2024-01-10",
            images = listOf("https://example.com/review2.jpg")
        ),
        Review(
            id = "r3",
            userName = "Sarah Ndongo",
            userAvatar = "https://example.com/avatar3.jpg",
            rating = 5.0f,
            comment = "Perfect beach vacation! Resort was clean, staff friendly, and the beach was gorgeous.",
            date = "2024-01-08",
            images = listOf("https://example.com/review3.jpg")
        )
    )
}
