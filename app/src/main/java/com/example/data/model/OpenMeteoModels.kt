package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de datos para Open-Meteo API pública directa (sin requerir API key)
 * Permite obtener datos meteorológicos reales en tiempo real inmediatamente vía Retrofit.
 */
@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "elevation") val elevation: Double? = null,
    @Json(name = "current_weather") val currentWeather: CurrentWeatherMeteo? = null,
    @Json(name = "hourly") val hourly: HourlyMeteo? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherMeteo(
    @Json(name = "temperature") val temperature: Double,
    @Json(name = "windspeed") val windspeed: Double,
    @Json(name = "winddirection") val winddirection: Double,
    @Json(name = "weathercode") val weathercode: Int,
    @Json(name = "time") val time: String
)

@JsonClass(generateAdapter = true)
data class HourlyMeteo(
    @Json(name = "time") val time: List<String>? = null,
    @Json(name = "temperature_2m") val temperature2m: List<Double>? = null,
    @Json(name = "relativehumidity_2m") val relativeHumidity2m: List<Int>? = null,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>? = null
)
