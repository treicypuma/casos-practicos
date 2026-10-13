package com.example.data.api

import com.example.data.model.NetworkTrafficLog
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * Cliente Singleton de Retrofit.
 * Demuestra formalmente:
 * - Configuración del Retrofit.Builder
 * - Asignación de Base URL canónica
 * - Conexión del conversor JSON (Moshi con soporte para Kotlin)
 * - Interceptor de red OkHttp para inspección de peticiones en tiempo real
 */
object RetrofitClient {

    const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"
    const val OPEN_METEO_BASE_URL = "https://api.open-meteo.com/"

    // Lista en memoria de logs de tráfico de red para inspección pedagógica
    private val _trafficLogs = CopyOnWriteArrayList<NetworkTrafficLog>()
    val trafficLogs: List<NetworkTrafficLog> get() = _trafficLogs.toList()

    private var onLogAddedCallback: ((NetworkTrafficLog) -> Unit)? = null

    fun setTrafficLogCallback(callback: (NetworkTrafficLog) -> Unit) {
        onLogAddedCallback = callback
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val networkInspectionInterceptor = Interceptor { chain ->
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        var response: Response? = null
        var errorMsg: String? = null
        var statusCode = 0
        var bodySample = ""

        try {
            response = chain.proceed(request)
            statusCode = response.code
            val duration = System.currentTimeMillis() - startTime

            // Clonar o leer una muestra segura del body de respuesta
            val responseBody = response.body
            val source = responseBody?.source()
            source?.request(Long.MAX_VALUE) // Buffer the entire body
            val buffer = source?.buffer?.clone()
            val fullString = buffer?.readUtf8() ?: ""
            bodySample = if (fullString.length > 500) {
                fullString.take(500) + " ... [truncado para visualización]"
            } else {
                fullString
            }

            val logEntry = NetworkTrafficLog(
                id = UUID.randomUUID().toString(),
                timestamp = timestamp,
                endpoint = request.url.encodedPath,
                fullUrl = request.url.toString(),
                method = request.method,
                statusCode = statusCode,
                durationMs = duration,
                isSuccess = response.isSuccessful,
                responseBodySample = bodySample
            )
            _trafficLogs.add(0, logEntry)
            if (_trafficLogs.size > 20) _trafficLogs.removeAt(_trafficLogs.lastIndex)
            onLogAddedCallback?.invoke(logEntry)

            return@Interceptor response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            errorMsg = e.localizedMessage ?: e.javaClass.simpleName
            val logEntry = NetworkTrafficLog(
                id = UUID.randomUUID().toString(),
                timestamp = timestamp,
                endpoint = request.url.encodedPath,
                fullUrl = request.url.toString(),
                method = request.method,
                statusCode = if (statusCode != 0) statusCode else 500,
                durationMs = duration,
                isSuccess = false,
                responseBodySample = "Error de red: $errorMsg",
                errorDetails = errorMsg
            )
            _trafficLogs.add(0, logEntry)
            if (_trafficLogs.size > 20) _trafficLogs.removeAt(_trafficLogs.lastIndex)
            onLogAddedCallback?.invoke(logEntry)
            throw e
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .addInterceptor(networkInspectionInterceptor)
            .build()
    }

    // Instancia Retrofit para OpenWeatherMap
    val openWeatherApi: OpenWeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenWeatherApiService::class.java)
    }

    // Instancia Retrofit para Open-Meteo
    val openMeteoApi: OpenMeteoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_METEO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenMeteoApiService::class.java)
    }

    fun recordManualLog(log: NetworkTrafficLog) {
        _trafficLogs.add(0, log)
        if (_trafficLogs.size > 20) _trafficLogs.removeAt(_trafficLogs.lastIndex)
        onLogAddedCallback?.invoke(log)
    }
}
