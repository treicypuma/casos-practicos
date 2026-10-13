package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de datos oficial de OpenWeatherMap (Endpoint /data/2.5/weather)
 * Mapeado utilizando Moshi para deserialización JSON automática.
 */
@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "coord") val coord: Coord? = null,
    @Json(name = "weather") val weather: List<WeatherDescription>? = null,
    @Json(name = "base") val base: String? = null,
    @Json(name = "main") val main: MainMetrics? = null,
    @Json(name = "visibility") val visibility: Int? = null,
    @Json(name = "wind") val wind: WindMetrics? = null,
    @Json(name = "clouds") val clouds: CloudMetrics? = null,
    @Json(name = "dt") val dt: Long? = null,
    @Json(name = "sys") val sys: SysMetrics? = null,
    @Json(name = "timezone") val timezone: Int? = null,
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "cod") val cod: Int? = null
)

@JsonClass(generateAdapter = true)
data class Coord(
    @Json(name = "lon") val lon: Double,
    @Json(name = "lat") val lat: Double
)

@JsonClass(generateAdapter = true)
data class WeatherDescription(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

@JsonClass(generateAdapter = true)
data class MainMetrics(
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double? = null,
    @Json(name = "temp_min") val tempMin: Double? = null,
    @Json(name = "temp_max") val tempMax: Double? = null,
    @Json(name = "pressure") val pressure: Int? = null,
    @Json(name = "humidity") val humidity: Int? = null
)

@JsonClass(generateAdapter = true)
data class WindMetrics(
    @Json(name = "speed") val speed: Double? = null,
    @Json(name = "deg") val deg: Int? = null
)

@JsonClass(generateAdapter = true)
data class CloudMetrics(
    @Json(name = "all") val all: Int? = null
)

@JsonClass(generateAdapter = true)
data class SysMetrics(
    @Json(name = "country") val country: String? = null,
    @Json(name = "sunrise") val sunrise: Long? = null,
    @Json(name = "sunset") val sunset: Long? = null
)
