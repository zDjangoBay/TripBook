package com.android.tripbook.model

data class WeatherData(
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)

data class CurrentWeather(
    val temperature: Int,
    val condition: String,
    val highTemp: Int,
    val lowTemp: Int,
    val feelsLike: Int,
    val alert: String = ""
)

data class HourlyForecast(
    val time: String,
    val temperature: Int,
    val condition: String,
    val precipitation: Int = 0
)

data class DailyForecast(
    val day: String,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: String,
    val precipitation: Int = 0
)
