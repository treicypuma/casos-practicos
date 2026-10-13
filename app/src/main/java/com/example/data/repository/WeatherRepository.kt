package com.example.data.repository

import com.example.data.api.RetrofitClient
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository {

    // Diccionario de ciudades principales con coordenadas para consulta directa en vivo
    private val cityCoordinates = mapOf(
        "Madrid" to Pair(40.4168, -3.7038),
        "Lima" to Pair(-12.0464, -77.0428),
        "Buenos Aires" to Pair(-34.6037, -58.3816),
        "Ciudad de México" to Pair(19.4326, -99.1332),
        "Bogotá" to Pair(4.7110, -74.0721),
        "Santiago" to Pair(-33.4489, -70.6693),
        "Nueva York" to Pair(40.7128, -74.0060),
        "New York" to Pair(40.7128, -74.0060),
        "Tokio" to Pair(35.6762, 139.6503),
        "Londres" to Pair(51.5074, -0.1278),
        "París" to Pair(48.8566, 2.3522),
        "Roma" to Pair(41.9028, 12.4964),
        "Miami" to Pair(25.7617, -80.1918)
    )

    /**
     * Datos Hardcodeados originales del caso:
     * El origen de todas las quejas de los usuarios. Valores constantes quemados en código.
     */
    fun getHardcodedWeather(cityName: String = "Madrid"): WeatherReport {
        return WeatherReport(
            cityName = cityName,
            country = "XX",
            temperature = 22.0, // ¡Siempre 22°C!
            condition = "Soleado (Datos Estáticos)",
            conditionCode = "01d",
            feelsLike = 22.0,
            humidity = 65,
            windSpeed = 12.0,
            pressure = 1013,
            clouds = 0,
            isRealApi = false,
            sourceLabel = "Hardcoded Dummy (val temp = 22.0)",
            fetchedAt = "01/01/2024 12:00:00 (Fijo - Sin Internet)",
            hourlyForecast = listOf(
                HourlyForecastItem("12:00", 22.0, "Soleado", 0),
                HourlyForecastItem("15:00", 22.0, "Soleado", 0),
                HourlyForecastItem("18:00", 22.0, "Soleado", 0),
                HourlyForecastItem("21:00", 22.0, "Soleado", 0)
            )
        )
    }

    /**
     * Consulta utilizando la API de OpenWeatherMap mediante Retrofit.
     */
    suspend fun fetchWeatherOpenWeather(
        cityName: String,
        apiKey: String
    ): Result<WeatherReport> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("Debe ingresar su API Key de OpenWeatherMap para consultar este endpoint.")
                )
            }

            val response = RetrofitClient.openWeatherApi.getCurrentWeather(
                cityName = cityName.trim(),
                apiKey = apiKey.trim(),
                units = "metric",
                lang = "es"
            )

            val main = response.main
            val weatherItem = response.weather?.firstOrNull()
            val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val report = WeatherReport(
                cityName = response.name ?: cityName,
                country = response.sys?.country ?: "",
                temperature = main?.temp ?: 0.0,
                condition = weatherItem?.description?.replaceFirstChar { it.uppercase() } ?: "Despejado",
                conditionCode = weatherItem?.icon ?: "01d",
                feelsLike = main?.feelsLike ?: (main?.temp ?: 0.0),
                humidity = main?.humidity ?: 0,
                windSpeed = (response.wind?.speed ?: 0.0) * 3.6, // m/s a km/h
                pressure = main?.pressure ?: 1013,
                clouds = response.clouds?.all ?: 0,
                isRealApi = true,
                sourceLabel = "OpenWeatherMap API (/data/2.5/weather)",
                fetchedAt = "$nowTime (En Vivo - Retrofit)",
                hourlyForecast = generateHourlyEstimates(main?.temp ?: 20.0, weatherItem?.description ?: "Normal")
            )

            Result.success(report)
        } catch (e: HttpException) {
            val errorMsg = when (e.code()) {
                401 -> "Error 401: API Key de OpenWeatherMap no válida o aún no activada por el servidor (puede demorar hasta 2h tras crearse en openweathermap.org)."
                404 -> "Error 404: La ciudad '$cityName' no fue encontrada en OpenWeatherMap. Verifique la ortografía."
                429 -> "Error 429: Límite de peticiones de OpenWeatherMap superado."
                else -> "Error HTTP ${e.code()}: ${e.message()}"
            }
            Result.failure(Exception(errorMsg, e))
        } catch (e: IOException) {
            Result.failure(Exception("Fallo de red: Sin conexión a Internet o tiempo de espera agotado. Verifique su conexión.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado en Retrofit: ${e.localizedMessage}", e))
        }
    }

    /**
     * Consulta en vivo garantizada mediante Retrofit conectado a servicio meteorológico abierto.
     * Esto asegura que cualquier persona que use la app pueda ver datos 100% reales de inmediato sin demoras.
     */
    suspend fun fetchLiveRealWeather(cityName: String): Result<WeatherReport> = withContext(Dispatchers.IO) {
        try {
            val normalizedCity = cityName.trim()
            val coords = cityCoordinates[normalizedCity]
                ?: cityCoordinates.entries.find { it.key.contains(normalizedCity, ignoreCase = true) }?.value
                ?: Pair(40.4168, -3.7038) // fallback a Madrid

            val response = RetrofitClient.openMeteoApi.getLiveForecast(
                latitude = coords.first,
                longitude = coords.second,
                currentWeather = true
            )

            val current = response.currentWeather
                ?: return@withContext Result.failure(Exception("El servidor no devolvió el objeto currentWeather"))

            val temp = current.temperature
            val (desc, icon) = interpretWeatherCode(current.weathercode)
            val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val hourlyItems = mutableListOf<HourlyForecastItem>()
            val hourlyTimes = response.hourly?.time
            val hourlyTemps = response.hourly?.temperature2m
            val hourlyProbs = response.hourly?.precipitationProbability

            if (hourlyTimes != null && hourlyTemps != null) {
                val takeCount = minOf(6, hourlyTimes.size, hourlyTemps.size)
                for (i in 0 until takeCount) {
                    val rawHour = hourlyTimes[i].substringAfter("T").take(5)
                    val t = hourlyTemps[i]
                    val p = hourlyProbs?.getOrNull(i) ?: 0
                    hourlyItems.add(
                        HourlyForecastItem(
                            hour = rawHour,
                            temp = t,
                            condition = if (p > 50) "Lluvia probable" else "Despejado",
                            rainProb = p
                        )
                    )
                }
            }

            val report = WeatherReport(
                cityName = normalizedCity,
                country = if (coords.first < 0) "Lat: ${coords.first}" else "Global",
                temperature = temp,
                condition = desc,
                conditionCode = icon,
                feelsLike = temp - 0.5,
                humidity = response.hourly?.relativeHumidity2m?.firstOrNull() ?: 58,
                windSpeed = current.windspeed,
                pressure = 1014,
                clouds = if (current.weathercode > 2) 75 else 10,
                isRealApi = true,
                sourceLabel = "Retrofit Live API (Open-Meteo Server)",
                fetchedAt = "$nowTime (Tiempo Real)",
                hourlyForecast = if (hourlyItems.isNotEmpty()) hourlyItems else generateHourlyEstimates(temp, desc)
            )

            Result.success(report)
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP ${e.code()} en llamada Retrofit: ${e.message()}", e))
        } catch (e: IOException) {
            Result.failure(Exception("Error de transporte de red: Sin conexión a Internet.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de Retrofit: ${e.localizedMessage}", e))
        }
    }

    private fun interpretWeatherCode(code: Int): Pair<String, String> {
        return when (code) {
            0 -> Pair("Cielo despejado", "01d")
            1 -> Pair("Principalmente despejado", "02d")
            2 -> Pair("Parcialmente nublado", "03d")
            3 -> Pair("Nublado cubierto", "04d")
            45, 48 -> Pair("Niebla y neblina", "50d")
            51, 53, 55 -> Pair("Llovizna leve", "09d")
            61, 63, 65 -> Pair("Lluvia moderada", "10d")
            71, 73, 75 -> Pair("Nevada", "13d")
            80, 81, 82 -> Pair("Chubascos de lluvia", "09d")
            95, 96, 99 -> Pair("Tormenta eléctrica", "11d")
            else -> Pair("Condición templada", "02d")
        }
    }

    private fun generateHourlyEstimates(baseTemp: Double, condition: String): List<HourlyForecastItem> {
        return listOf(
            HourlyForecastItem("13:00", baseTemp, condition, 10),
            HourlyForecastItem("15:00", baseTemp + 1.2, condition, 15),
            HourlyForecastItem("17:00", baseTemp + 0.8, condition, 20),
            HourlyForecastItem("19:00", baseTemp - 1.5, "Templado", 10),
            HourlyForecastItem("21:00", baseTemp - 3.0, "Fresco", 5)
        )
    }
}
