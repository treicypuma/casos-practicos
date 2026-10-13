package com.example.data.api

import com.example.data.model.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz alternativa de Retrofit conectada a la API meteorológica global abierta de Open-Meteo.
 * Permite realizar llamadas reales inmediatamente sin exigir obligatoriamente una clave comercial de OpenWeatherMap.
 */
interface OpenMeteoApiService {

    @GET("v1/forecast")
    suspend fun getLiveForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,relativehumidity_2m,precipitation_probability"
    ): OpenMeteoResponse
}
