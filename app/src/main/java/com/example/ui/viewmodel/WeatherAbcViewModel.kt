package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitClient
import com.example.data.model.AbcCaseData
import com.example.data.model.NetworkTrafficLog
import com.example.data.model.TeamRole
import com.example.data.model.WeatherReport
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val iconName: String) {
    ABC_CASE("Caso ABC", "Assignment"),
    WEATHER_APP("App Clima", "WbSunny"),
    CODE_INSPECTOR("Código & Red", "Code"),
    TIMER_WORKSHOP("Taller 30 min", "Timer")
}

enum class ApiProvider {
    OPEN_METEO_LIVE,
    OPEN_WEATHER_MAP
}

data class WeatherAbcUiState(
    val currentTab: AppNavTab = AppNavTab.ABC_CASE,
    val currentPhaseNumber: Int = 1,
    // Weather App state
    val isHardcodedMode: Boolean = true, // Inicia mostrando el problema de datos hardcodeados
    val selectedCity: String = "Madrid",
    val currentWeather: WeatherReport? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val openWeatherApiKey: String = "",
    val apiProvider: ApiProvider = ApiProvider.OPEN_METEO_LIVE,
    // Tráfico de red
    val trafficLogs: List<NetworkTrafficLog> = emptyList(),
    // Checklist y progreso del caso
    val checkedItems: Set<String> = emptySet(),
    val completedPhases: Set<Int> = emptySet(),
    val phaseNotes: Map<Int, String> = emptyMap(),
    // Timer para taller de 30 minutos
    val timerSecondsLeft: Int = 30 * 60, // 30 minutos
    val isTimerActive: Boolean = false,
    val currentRoleIndex: Int = 0,
    val basicCallExecutionResult: String? = null,
    val isBasicCallRunning: Boolean = false
)

class WeatherAbcViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow(WeatherAbcUiState())
    val uiState: StateFlow<WeatherAbcUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    val teamRoles = listOf(
        TeamRole(
            roleTitle = "Líder Técnico & Facilitador",
            memberName = "Estudiante 1",
            responsibilities = "Gestiona los 30 minutos de tiempo, modera la discusión y asegura el cumplimiento de cada fase.",
            phaseFocus = "Fase 1 (Lectura) y Fase 5 (Conclusiones)"
        ),
        TeamRole(
            roleTitle = "Arquitecto de Software",
            memberName = "Estudiante 2",
            responsibilities = "Diseña la Base URL, estructura del Builder, OkHttpClient y las data classes del JSON.",
            phaseFocus = "Fase 2 (Información) y Fase 3 (Análisis)"
        ),
        TeamRole(
            roleTitle = "Desarrollador Android (Retrofit)",
            memberName = "Estudiante 3",
            responsibilities = "Implementa la interfaz @GET, configura los parámetros @Query y programa la corrutina en el ViewModel.",
            phaseFocus = "Fase 4 (Solución: Código Retrofit)"
        ),
        TeamRole(
            roleTitle = "Analista QA & Redes",
            memberName = "Estudiante 4",
            responsibilities = "Prueba casos límite, valida códigos HTTP (200, 401, 404), inspecciona logs de red y evalúa la satisfacción final.",
            phaseFocus = "Fase 3 (Manejo de Errores) y Fase 5 (Pruebas en vivo)"
        )
    )

    init {
        // Cargar clima inicial hardcodeado para ilustrar el problema
        loadWeather()

        // Escuchar logs de red en vivo desde RetrofitClient
        RetrofitClient.setTrafficLogCallback { newLog ->
            _uiState.update { current ->
                val updatedLogs = (listOf(newLog) + current.trafficLogs).distinctBy { it.id }.take(25)
                current.copy(trafficLogs = updatedLogs)
            }
        }
    }

    fun setTab(tab: AppNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setPhase(phaseNum: Int) {
        if (phaseNum in 1..5) {
            _uiState.update { it.copy(currentPhaseNumber = phaseNum) }
        }
    }

    fun toggleHardcodedMode(enableHardcoded: Boolean) {
        _uiState.update { it.copy(isHardcodedMode = enableHardcoded) }
        loadWeather()
    }

    fun setCity(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
        loadWeather()
    }

    fun setOpenWeatherApiKey(key: String) {
        _uiState.update { it.copy(openWeatherApiKey = key) }
    }

    fun setApiProvider(provider: ApiProvider) {
        _uiState.update { it.copy(apiProvider = provider) }
        loadWeather()
    }

    fun toggleChecklistItem(item: String) {
        _uiState.update { current ->
            val set = current.checkedItems.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            current.copy(checkedItems = set)
        }
    }

    fun markPhaseCompleted(phaseNum: Int) {
        _uiState.update { current ->
            val set = current.completedPhases.toMutableSet()
            set.add(phaseNum)
            val nextPhase = if (phaseNum < 5) phaseNum + 1 else phaseNum
            current.copy(
                completedPhases = set,
                currentPhaseNumber = nextPhase
            )
        }
    }

    fun updatePhaseNote(phaseNum: Int, note: String) {
        _uiState.update { current ->
            val notes = current.phaseNotes.toMutableMap()
            notes[phaseNum] = note
            current.copy(phaseNotes = notes)
        }
    }

    fun selectRole(index: Int) {
        if (index in teamRoles.indices) {
            _uiState.update { it.copy(currentRoleIndex = index) }
        }
    }

    fun loadWeather() {
        val state = _uiState.value
        if (state.isHardcodedMode) {
            // Modo Hardcodeado: Datos estáticos fijos
            val hardcoded = repository.getHardcodedWeather(state.selectedCity)
            _uiState.update {
                it.copy(
                    currentWeather = hardcoded,
                    isLoading = false,
                    errorMessage = null
                )
            }
            return
        }

        // Modo Retrofit API Real
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (state.apiProvider == ApiProvider.OPEN_WEATHER_MAP && state.openWeatherApiKey.isNotBlank()) {
                repository.fetchWeatherOpenWeather(state.selectedCity, state.openWeatherApiKey)
            } else {
                repository.fetchLiveRealWeather(state.selectedCity)
            }

            result.fold(
                onSuccess = { report ->
                    _uiState.update {
                        it.copy(
                            currentWeather = report,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al consultar Retrofit"
                        )
                    }
                }
            )
        }
    }

    /**
     * Ejecuta una llamada básica en vivo para cumplir con la Fase 4 del caso.
     */
    fun executeBasicRetrofitCall(cityName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBasicCallRunning = true, basicCallExecutionResult = null) }
            val startTime = System.currentTimeMillis()

            try {
                val state = _uiState.value
                val reportResult = if (state.openWeatherApiKey.isNotBlank()) {
                    repository.fetchWeatherOpenWeather(cityName, state.openWeatherApiKey)
                } else {
                    repository.fetchLiveRealWeather(cityName)
                }

                val latency = System.currentTimeMillis() - startTime
                reportResult.fold(
                    onSuccess = { report ->
                        val output = """
                            HTTP/1.1 200 OK
                            Latencia: ${latency}ms
                            Servidor: ${report.sourceLabel}
                            Ciudad: ${report.cityName} (${report.country})
                            Temperatura: ${report.temperature}°C
                            Sensación térmica: ${report.feelsLike}°C
                            Humedad: ${report.humidity}%
                            Condición: ${report.condition}
                            Viento: ${report.windSpeed} km/h
                            Resultado: ¡Llamada Retrofit exitosa! Datos deserializados en memoria.
                        """.trimIndent()
                        _uiState.update {
                            it.copy(
                                isBasicCallRunning = false,
                                basicCallExecutionResult = output,
                                currentWeather = report,
                                isHardcodedMode = false
                            )
                        }
                    },
                    onFailure = { error ->
                        val output = """
                            FALLO EN LLAMADA RETROFIT
                            Latencia: ${latency}ms
                            Causa: ${error.message}
                            Sugerencia técnica: Revise conexión a internet, permisos en AndroidManifest.xml o validez de la API Key.
                        """.trimIndent()
                        _uiState.update {
                            it.copy(
                                isBasicCallRunning = false,
                                basicCallExecutionResult = output
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isBasicCallRunning = false,
                        basicCallExecutionResult = "Excepción en Corrutina: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // Control de temporizador
    fun toggleTimer() {
        val willActive = !_uiState.value.isTimerActive
        _uiState.update { it.copy(isTimerActive = willActive) }

        if (willActive) {
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_uiState.value.isTimerActive && _uiState.value.timerSecondsLeft > 0) {
                    delay(1000)
                    _uiState.update { current ->
                        val nextSec = current.timerSecondsLeft - 1
                        current.copy(
                            timerSecondsLeft = nextSec,
                            isTimerActive = nextSec > 0
                        )
                    }
                }
            }
        } else {
            timerJob?.cancel()
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isTimerActive = false,
                timerSecondsLeft = 30 * 60
            )
        }
    }

    fun setTimerToPhaseMinutes(minutes: Int) {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isTimerActive = false,
                timerSecondsLeft = minutes * 60
            )
        }
    }

    fun generateFinalReportMarkdown(): String {
        val state = _uiState.value
        val sb = StringBuilder()
        sb.append("# INFORME FINAL - CASO DE ESTUDIO ABC: CLIMA & RETROFIT\n\n")
        sb.append("**Metodología:** Aprendizaje Basado en Casos (ABC)\n")
        sb.append("**Tiempo Total del Taller:** 30 minutos (Completadas: ${state.completedPhases.size}/5 fases)\n\n")
        sb.append("## 1. Problema Identificado\n")
        sb.append("Los usuarios experimentaban frustración por datos meteorológicos fijos (22°C estático en cualquier ciudad) debido a variables hardcodeadas en la interfaz de usuario.\n\n")
        sb.append("## 2. Solución de Arquitectura Implementada\n")
        sb.append("- **Librería Cliente:** Retrofit 2 + OkHttp 4 con HttpLoggingInterceptor.\n")
        sb.append("- **Base URL:** `https://api.openweathermap.org/`\n")
        sb.append("- **Endpoint:** `@GET(\"data/2.5/weather\")` con parámetros `@Query` (`q`, `appid`, `units=metric`).\n")
        sb.append("- **Conversor JSON:** Moshi con `KotlinJsonAdapterFactory` para deserialización estricta.\n")
        sb.append("- **Manejo de Errores:** Códigos HTTP 401, 404 y fallos de transporte `IOException` capturados y notificados.\n\n")
        sb.append("## 3. Notas del Equipo por Fase\n")
        AbcCaseData.phases.forEach { p ->
            val note = state.phaseNotes[p.phaseNumber] ?: "Sin notas registradas."
            val completed = if (state.completedPhases.contains(p.phaseNumber)) "✅ Concluida" else "⏳ Pendiente"
            sb.append("### Fase ${p.phaseNumber}: ${p.title} (${p.durationMinutes} min) - $completed\n")
            sb.append("**Objetivo:** ${p.summary}\n")
            sb.append("**Aportes del Equipo:** $note\n\n")
        }
        sb.append("## 4. Conclusión de Desempeño\n")
        sb.append("La migración de datos hardcodeados a Retrofit restauró la fidelidad de la información y eliminó el descontento de los usuarios.\n")
        return sb.toString()
    }
}
