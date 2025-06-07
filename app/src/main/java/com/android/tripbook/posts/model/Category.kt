package com.android.tripbook.posts.model

enum class Category(val displayName: String, val icon: String) {
    ADVENTURE("Adventure", "🏔️"),
    CULTURE("Culture", "🏛️"),
    FOOD("Food", "🍽️"),
    NATURE("Nature", "🌿"),
    CITY("City", "🏙️"),
    BEACH("Beach", "🏖️"),
    MOUNTAIN("Mountain", "⛰️"),
    WILDLIFE("Wildlife", "🦁"),
    HISTORY("History", "📜"),
    PHOTOGRAPHY("Photography", "📸"),
    BUDGET("Budget Travel", "💰"),
    LUXURY("Luxury", "✨"),
    SOLO("Solo Travel", "🚶"),
    FAMILY("Family", "👨‍👩‍👧‍👦"),
    COUPLE("Couple", "💑"),
    BACKPACKING("Backpacking", "🎒"),
    ROAD_TRIP("Road Trip", "🚗"),
    OTHER("Other", "📍")
}
