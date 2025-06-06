package com.android.tripbook.model

// This file can be deleted as CurrentWeather is already defined in WeatherData.kt
// If you need to add functionality to CurrentWeather, use extension functions instead

// Example of extension functions if needed:
fun CurrentWeather.getTemperatureInFahrenheit(): Int {
    return (this.temperature * 9/5) + 32
}

fun CurrentWeather.getWeatherIconName(): String {
    return when (this.condition.lowercase()) {
        "clear", "sunny" -> "ic_sunny"
        "clouds", "partly cloudy", "cloudy" -> "ic_cloudy"
        "rain", "drizzle", "shower rain" -> "ic_rainy"
        "thunderstorm" -> "ic_thunderstorm"
        "snow" -> "ic_snowy"
        "mist", "fog" -> "ic_foggy"
        else -> "ic_sunny"
    }
}
