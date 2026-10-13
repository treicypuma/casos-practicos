package com.example.data.model

data class WeatherReport(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val condition: String,
    val conditionCode: String,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val clouds: Int,
    val isRealApi: Boolean,
    val sourceLabel: String,
    val fetchedAt: String,
    val hourlyForecast: List<HourlyForecastItem> = emptyList()
)

data class HourlyForecastItem(
    val hour: String,
    val temp: Double,
    val condition: String,
    val rainProb: Int
)

data class NetworkTrafficLog(
    val id: String,
    val timestamp: String,
    val endpoint: String,
    val fullUrl: String,
    val method: String,
    val statusCode: Int,
    val durationMs: Long,
    val isSuccess: Boolean,
    val responseBodySample: String,
    val errorDetails: String? = null
)
