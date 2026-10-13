package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkTrafficLog
import com.example.ui.components.CodeBlockCard
import com.example.ui.components.FormalHeader
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold600
import com.example.ui.theme.Navy700
import com.example.ui.theme.Ruby500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import androidx.compose.ui.draw.clip
import com.example.ui.theme.Slate950
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.WeatherAbcUiState
import com.example.ui.viewmodel.WeatherAbcViewModel

enum class CodeInspectionFile(val title: String, val fileName: String, val category: String) {
    INTERFACE("1. Interface Retrofit", "OpenWeatherApiService.kt", "Contrato HTTP"),
    BUILDER("2. Retrofit.Builder", "RetrofitClient.kt", "Singleton & OkHttp"),
    DTO_MODELS("3. Modelo JSON (DTO)", "OpenWeatherModels.kt", "Deserialización Moshi"),
    REPOSITORY("4. Repositorio & Errores", "WeatherRepository.kt", "Capa de Datos & Result<T>")
}

@Composable
fun CodeArchitectureScreen(
    state: WeatherAbcUiState,
    viewModel: WeatherAbcViewModel,
    modifier: Modifier = Modifier
) {
    var selectedFile by remember { mutableStateOf(CodeInspectionFile.INTERFACE) }
    var testCityInput by remember { mutableStateOf("Madrid") }
    var expandedLogId by remember { mutableStateOf<String?>(null) }

    val fileContents = mapOf(
        CodeInspectionFile.INTERFACE to """
            // OpenWeatherApiService.kt
            package com.example.data.api

            import com.example.data.model.WeatherResponse
            import retrofit2.http.GET
            import retrofit2.http.Query

            /**
             * Interfaz de contrato HTTP para Retrofit
             * - Anotación @GET: Define el verbo HTTP y la ruta relativa al baseUrl
             * - Anotación @Query: Genera parámetros en la URL (?q=Madrid&appid=KEY&units=metric)
             * - suspend fun: Ejecución asíncrona no bloqueante compatible con Coroutines
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
        """.trimIndent(),

        CodeInspectionFile.BUILDER to """
            // RetrofitClient.kt
            package com.example.data.api

            import com.squareup.moshi.Moshi
            import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
            import okhttp3.OkHttpClient
            import okhttp3.logging.HttpLoggingInterceptor
            import retrofit2.Retrofit
            import retrofit2.converter.moshi.MoshiConverterFactory

            object RetrofitClient {
                // 1. Base URL canónica (obligatoria la barra final '/')
                private const val BASE_URL = "https://api.openweathermap.org/"

                // 2. Moshi con adaptador reflexivo para Kotlin
                private val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                // 3. OkHttpClient con Logging Interceptor para depuración
                private val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()

                // 4. Construcción Singleton de Retrofit
                val weatherApi: OpenWeatherApiService by lazy {
                    Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()
                        .create(OpenWeatherApiService::class.java)
                }
            }
        """.trimIndent(),

        CodeInspectionFile.DTO_MODELS to """
            // OpenWeatherModels.kt
            package com.example.data.model

            import com.squareup.moshi.Json
            import com.squareup.moshi.JsonClass

            /**
             * Mapeo estricto del JSON retornado por OpenWeatherMap
             */
            @JsonClass(generateAdapter = true)
            data class WeatherResponse(
                @Json(name = "name") val name: String?,
                @Json(name = "coord") val coord: Coord?,
                @Json(name = "main") val main: MainMetrics?,
                @Json(name = "weather") val weather: List<WeatherDescription>?,
                @Json(name = "wind") val wind: WindMetrics?,
                @Json(name = "cod") val cod: Int?
            )

            @JsonClass(generateAdapter = true)
            data class MainMetrics(
                @Json(name = "temp") val temp: Double,
                @Json(name = "feels_like") val feelsLike: Double?,
                @Json(name = "humidity") val humidity: Int?,
                @Json(name = "pressure") val pressure: Int?
            )

            @JsonClass(generateAdapter = true)
            data class WeatherDescription(
                @Json(name = "id") val id: Int,
                @Json(name = "main") val main: String,
                @Json(name = "description") val description: String,
                @Json(name = "icon") val icon: String
            )
        """.trimIndent(),

        CodeInspectionFile.REPOSITORY to """
            // WeatherRepository.kt
            package com.example.data.repository

            import com.example.data.api.RetrofitClient
            import com.example.data.model.WeatherReport
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.withContext
            import retrofit2.HttpException
            import java.io.IOException

            class WeatherRepository {

                suspend fun fetchWeather(city: String, apiKey: String): Result<WeatherReport> =
                    withContext(Dispatchers.IO) {
                        try {
                            // 1. Llamada suspendida a la interfaz Retrofit
                            val response = RetrofitClient.openWeatherApi.getCurrentWeather(
                                cityName = city,
                                apiKey = apiKey,
                                units = "metric"
                            )
                            // 2. Transformar DTO de red a modelo de dominio limpio
                            val report = mapToDomain(response)
                            Result.success(report)
                        } catch (e: HttpException) {
                            // 3. Manejo de códigos HTTP de error
                            val msg = when (e.code()) {
                                401 -> "Error 401: API Key no autorizada o inactiva"
                                404 -> "Error 404: Ciudad no encontrada en el servidor"
                                else -> "Error HTTP: ${"$"}{e.code()}"
                            }
                            Result.failure(Exception(msg, e))
                        } catch (e: IOException) {
                            // 4. Manejo de errores de transporte de red (sin conexión)
                            Result.failure(Exception("Sin conexión a Internet", e))
                        }
                    }
            }
        """.trimIndent()
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FormalHeader(
                title = "Código & Red",
                subtitle = "Inspección de Interfaces Retrofit, Builders y Peticiones HTTP",
                badgeText = "FASE 4: SOLUCIÓN",
                badgeColor = Navy700
            )

            // Selector de Archivos
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CodeInspectionFile.entries.toTypedArray()) { file ->
                    val isSelected = selectedFile == file
                    Surface(
                        color = if (isSelected) Navy700 else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .clickable { selectedFile = file }
                            .testTag("file_tab_${file.name}")
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = file.title,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = file.fileName,
                                fontSize = 10.sp,
                                color = if (isSelected) Gold400 else Slate400
                            )
                        }
                    }
                }
            }
        }

        item {
            // Visualizador de Código
            CodeBlockCard(
                title = selectedFile.fileName,
                code = fileContents[selectedFile] ?: ""
            )
        }

        item {
            // Banco de Pruebas: Llamada Básica Retrofit (Fase 4 del Caso)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("basic_call_sandbox_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ LABORATORIO: LLAMADA BÁSICA RETROFIT",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Gold400
                        )
                        Surface(
                            color = Emerald500.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "FASE 4 (10 min)",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ejecuta una petición HTTP GET real utilizando la interface configurada para verificar el apretón de manos (handshake) y deserialización.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = testCityInput,
                            onValueChange = { testCityInput = it },
                            label = { Text("Ciudad de Prueba") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_city_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.executeBasicRetrofitCall(testCityInput) },
                            enabled = !state.isBasicCallRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            modifier = Modifier.testTag("execute_basic_call_btn")
                        ) {
                            if (state.isBasicCallRunning) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Llamar @GET", fontSize = 12.sp)
                            }
                        }
                    }

                    if (state.basicCallExecutionResult != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate950)
                                .padding(12.dp)
                                .testTag("basic_call_result_box")
                        ) {
                            Text(
                                text = state.basicCallExecutionResult ?: "",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Emerald500,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            // Historial de Tráfico de Red (OkHttp Interceptor)
            Text(
                text = "LOGS DE RED INTERCEPTADOS (OKHTTP TRAFFIC)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate400
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (state.trafficLogs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay peticiones HTTP registradas aún. Ejecuta una llamada arriba.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            } else {
                state.trafficLogs.take(6).forEach { log ->
                    val isExpanded = expandedLogId == log.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                expandedLogId = if (isExpanded) null else log.id
                            }
                            .testTag("log_item_${log.id.take(6)}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (log.isSuccess) Emerald500 else Ruby500,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${log.method} ${log.statusCode}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = log.endpoint,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "${log.durationMs}ms • ${log.timestamp}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate400
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = log.fullUrl,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = Slate400,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 1
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    HorizontalDivider(color = Slate700)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "CUERPO DE RESPUESTA (RAW JSON):",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold600
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Slate900)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = log.responseBodySample,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = Slate200
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
