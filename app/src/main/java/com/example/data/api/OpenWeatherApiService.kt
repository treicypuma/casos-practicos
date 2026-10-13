package com.example.data.api

import com.example.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz oficial de Retrofit para OpenWeatherMap API.
 * Cumple con el requerimiento de la fase 4 del caso ABC:
 * - Anotación @GET con endpoint relativo "data/2.5/weather"
 * - Parámetros con anotaciones @Query
 * - Función suspendida (suspend fun) para ejecución asíncrona no bloqueante
 */
interface OpenWeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}
