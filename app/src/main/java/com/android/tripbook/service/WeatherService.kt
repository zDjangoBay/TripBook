package com.android.tripbook.service

import com.android.tripbook.model.CurrentWeather
import com.android.tripbook.model.DailyForecast
import com.android.tripbook.model.HourlyForecast
import com.android.tripbook.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalTime

class WeatherService {
    private val apiKey = "273872956475e457331de3574bbb5306"
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    
    suspend fun getWeatherForecast(location: String): WeatherData = withContext(Dispatchers.IO) {
        try {
            // Get coordinates for the location
            val geocodingUrl = "https://api.openweathermap.org/geo/1.0/direct?q=$location&limit=1&appid=$apiKey"
            val geocodingRequest = Request.Builder().url(geocodingUrl).build()
            val geocodingResponse = client.newCall(geocodingRequest).execute()
            val geocodingJson = geocodingResponse.body?.string() ?: throw Exception("Failed to get location coordinates")
            
            val geocodingResults = json.decodeFromString<List<GeocodingResult>>(geocodingJson)
            if (geocodingResults.isEmpty()) throw Exception("Location not found")
            
            val lat = geocodingResults[0].lat
            val lon = geocodingResults[0].lon
            
            // Get weather data using coordinates
            val weatherUrl = "https://api.openweathermap.org/data/3.0/onecall?lat=$lat&lon=$lon&exclude=minutely&units=metric&appid=$apiKey"
            val weatherRequest = Request.Builder().url(weatherUrl).build()
            val weatherResponse = client.newCall(weatherRequest).execute()
            val weatherJson = weatherResponse.body?.string() ?: throw Exception("Failed to get weather data")
            
            val weatherResult = json.decodeFromString<WeatherApiResponse>(weatherJson)
            
            // Convert API response to our model
            return@withContext mapToWeatherData(weatherResult)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to mock data if API call fails
            return@withContext createMockWeatherData()
        }
    }
    
    private fun mapToWeatherData(apiResponse: WeatherApiResponse): WeatherData {
        // Get timezone for proper time formatting
        val zoneId = ZoneId.of(apiResponse.timezone)
        
        // Current weather
        val current = CurrentWeather(
            temperature = apiResponse.current.temp.toInt(),
            condition = apiResponse.current.weather.firstOrNull()?.description?.capitalize() ?: "Unknown",
            highTemp = apiResponse.daily.firstOrNull()?.temp?.max?.toInt() ?: 0,
            lowTemp = apiResponse.daily.firstOrNull()?.temp?.min?.toInt() ?: 0,
            feelsLike = apiResponse.current.feelsLike.toInt(),
            alert = apiResponse.alerts?.firstOrNull()?.description ?: ""
        )
        
        // Find sunset time for today
        val sunsetTime = apiResponse.daily.firstOrNull()?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochSecond(it.sunset),
                zoneId
            )
        }
        
        // Hourly forecast (next 6 hours)
        val hourly = apiResponse.hourly.take(6).mapIndexed { index, hour ->
            val dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(hour.dt),
                zoneId
            )
            
            // Check if this hour is sunset time
            val isSunset = sunsetTime != null && 
                           dateTime.toLocalDate() == sunsetTime.toLocalDate() && 
                           Math.abs(dateTime.toLocalTime().toSecondOfDay() - sunsetTime.toLocalTime().toSecondOfDay()) < 1800 // Within 30 minutes
            
            val time = if (isSunset) {
                "${dateTime.format(DateTimeFormatter.ofPattern("h:mm"))} PM"
            } else {
                dateTime.format(DateTimeFormatter.ofPattern("h a"))
            }
            
            val condition = if (isSunset) "Sunset" else hour.weather.firstOrNull()?.main ?: "Unknown"
            
            HourlyForecast(
                time = time,
                temperature = hour.temp.toInt(),
                condition = condition,
                precipitation = (hour.pop * 100).toInt()
            )
        }
        
        // Daily forecast (3 days including yesterday)
        val yesterday = LocalDateTime.now(zoneId).minusDays(1).toLocalDate()
        val yesterdayForecast = DailyForecast(
            day = "Yesterday",
            highTemp = 28, // Mock data for yesterday
            lowTemp = 20,
            condition = "Sunny",
            precipitation = 0
        )
        
        val dailyForecasts = listOf(yesterdayForecast) + apiResponse.daily.take(2).mapIndexed { index, day ->
            val dayName = when(index) {
                0 -> "Today"
                else -> "Tomorrow"
            }
            
            DailyForecast(
                day = dayName,
                highTemp = day.temp.max.toInt(),
                lowTemp = day.temp.min.toInt(),
                condition = day.weather.firstOrNull()?.main ?: "Unknown",
                precipitation = (day.pop * 100).toInt()
            )
        }
        
        return WeatherData(current, hourly, dailyForecasts)
    }
    
    private fun createMockWeatherData(): WeatherData {
        return WeatherData(
            current = CurrentWeather(
                temperature = 27,
                condition = "Partly Cloudy",
                highTemp = 28,
                lowTemp = 20,
                feelsLike = 29,
                alert = "Showers ending early. Low 20C."
            ),
            hourly = listOf(
                HourlyForecast("5 PM", 26, "Partly Cloudy", 11),
                HourlyForecast("6 PM", 25, "Partly Cloudy", 9),
                HourlyForecast("6:23 PM", 25, "Sunset", 8),
                HourlyForecast("7 PM", 24, "Cloudy", 7),
                HourlyForecast("8 PM", 23, "Cloudy", 9),
                HourlyForecast("9 PM", 23, "Cloudy", 14)
            ),
            daily = listOf(
                DailyForecast("Yesterday", 28, 20, "Sunny", 0),
                DailyForecast("Today", 28, 20, "Partly Cloudy", 11),
                DailyForecast("Tomorrow", 27, 19, "Rainy", 40)
            )
        )
    }
    
    // Extension function to capitalize first letter of each word
    private fun String.capitalize(): String {
        return this.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
            }
        }
    }
}

// API response data classes
@Serializable
data class GeocodingResult(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

@Serializable
data class WeatherApiResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val current: CurrentWeatherApi,
    val hourly: List<HourlyWeatherApi>,
    val daily: List<DailyWeatherApi>,
    val alerts: List<WeatherAlert>? = null
)

@Serializable
data class CurrentWeatherApi(
    val dt: Long,
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    @SerialName("wind_speed") val windSpeed: Double,
    val weather: List<WeatherCondition>
)

@Serializable
data class HourlyWeatherApi(
    val dt: Long,
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    val pop: Double, // Probability of precipitation
    val weather: List<WeatherCondition>
)

@Serializable
data class DailyWeatherApi(
    val dt: Long,
    val temp: TempRange,
    val humidity: Int,
    val pop: Double, // Probability of precipitation
    val weather: List<WeatherCondition>,
    val sunrise: Long,
    val sunset: Long
)

@Serializable
data class TempRange(
    val min: Double,
    val max: Double,
    val day: Double,
    val night: Double
)

@Serializable
data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class WeatherAlert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)