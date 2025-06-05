package com.android.tripbook.model

import com.android.tripbook.model.WeatherEntry

data class WeatherEntry(
    val dt_txt: String,
    val main: Main,
    val weather: List<WeatherInfo>
)

data class Main(
    val temp: Double
)

data class WeatherInfo(
    val description: String
)