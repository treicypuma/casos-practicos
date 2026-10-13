package com.example.data.model

data class AbcPhaseInfo(
    val phaseNumber: Int,
    val title: String,
    val durationMinutes: Int,
    val summary: String,
    val coreQuestion: String,
    val keyConcepts: List<String>,
    val checklist: List<String>,
    val formalAnalysis: String,
    val codeSample: String
)

data class UserComplaint(
    val userName: String,
    val rating: Int,
    val date: String,
    val city: String,
    val comment: String,
    val issueType: String
)

data class TeamRole(
    val roleTitle: String,
    val memberName: String,
    val responsibilities: String,
    val phaseFocus: String
)

object AbcCaseData {
    val complaints = listOf(
        UserComplaint(
            userName = "Carlos M. (Madrid)",
            rating = 1,
            date = "Hace 2 horas",
            city = "Madrid",
            comment = "¡Qué vergüenza de app! Estamos a 38°C en plena ola de calor y la app sigue diciendo 22°C con brisa ligera. ¡No cambia nunca!",
            issueType = "Datos Falsos / Congelados"
        ),
        UserComplaint(
            userName = "Lucía P. (Lima)",
            rating = 1,
            date = "Ayer",
            city = "Lima",
            comment = "Está lloviendo torrencialmente afuera y la app me muestra un sol radiante de 22°C. Claramente los datos están grabados a mano en el código.",
            issueType = "Desincronización Total"
        ),
        UserComplaint(
            userName = "Santiago R. (Buenos Aires)",
            rating = 2,
            date = "Hace 3 días",
            city = "Buenos Aires",
            comment = "Busqué Tokio, Nueva York y Buenos Aires. ¡Las tres marcan exactamente los mismos 22°C y 65% de humedad! Es un insulto al usuario.",
            issueType = "Hardcoding Absoluto"
        ),
        UserComplaint(
            userName = "Mariana G. (Bogotá)",
            rating = 1,
            date = "Hace 4 días",
            city = "Bogotá",
            comment = "La instalé para mi viaje y casi pierdo mi vuelo por lluvia. ¿Cuándo van a conectarla a un servidor real como OpenWeatherMap?",
            issueType = "Falta de Retrofit / API Real"
        )
    )

    val phases = listOf(
        AbcPhaseInfo(
            phaseNumber = 1,
            title = "LECTURA",
            durationMinutes = 5,
            summary = "Identificación de necesidades técnicas fundamentales para abandonar los datos hardcodeados e iniciar la integración.",
            coreQuestion = "¿Qué elementos de arquitectura e infraestructura necesitamos antes de escribir una sola línea de Retrofit?",
            keyConcepts = listOf(
                "Base URL: https://api.openweathermap.org/",
                "Endpoint: /data/2.5/weather?q={city}&appid={key}",
                "Permisos del SO: <uses-permission android:name=\"android.permission.INTERNET\" />",
                "Conversor JSON: Moshi o Gson para serializar/deserializar",
                "Manejo de API Key segura"
            ),
            checklist = listOf(
                "Registrar o simular API Key de OpenWeatherMap",
                "Definir Base URL canónica con barra final obligatoria ('/')",
                "Declarar permiso INTERNET en AndroidManifest.xml",
                "Incorporar dependencias Retrofit y Moshi/Gson en build.gradle.kts",
                "Garantizar que no queden valores estáticos en la capa UI"
            ),
            formalAnalysis = """
                En esta primera fase del caso ABC, el equipo analiza la queja masiva de los usuarios. La aplicación actual tiene una variable fija `val temp = 22.0` en el Composable.
                Para migrar a una arquitectura cliente-servidor profesional, se requiere:
                1. Dirección Base (Base URL): Servidor raíz de OpenWeatherMap.
                2. Endpoint REST: Ruta específica del recurso de clima actual.
                3. Permiso en Manifiesto: Android bloquea cualquier socket de red sin el permiso INTERNET a nivel de kernel de Linux.
                4. Motor de Deserialización: Retrofit delega la transformación del String JSON a objetos fuertemente tipados a través de un Converter.Factory.
            """.trimIndent(),
            codeSample = """
                <!-- AndroidManifest.xml -->
                <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                    <!-- Permiso fundamental requerido -->
                    <uses-permission android:name="android.permission.INTERNET" />
                    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                    ...
                </manifest>
            """.trimIndent()
        ),
        AbcPhaseInfo(
            phaseNumber = 2,
            title = "INFORMACIÓN",
            durationMinutes = 5,
            summary = "Estudio formal de la anatomía de Retrofit: Interface, Builder y conversores de datos.",
            coreQuestion = "¿Cómo se estructura la librería Retrofit y cuál es la función de cada uno de sus componentes?",
            keyConcepts = listOf(
                "Interface: Contrato con anotaciones HTTP (@GET, @Query)",
                "Retrofit.Builder: Patrón constructor para configurar cliente",
                "Converter Factory: MoshiConverterFactory / GsonConverterFactory",
                "OkHttpClient: Motor HTTP subyacente con LoggingInterceptor",
                "Coroutines: 'suspend fun' para llamadas asíncronas no bloqueantes"
            ),
            checklist = listOf(
                "Comprender por qué la interfaz no tiene cuerpo de métodos",
                "Configurar Retrofit.Builder con baseUrl() y addConverterFactory()",
                "Reemplazar Call<T>.enqueue por funciones suspendidas 'suspend fun'",
                "Adjuntar HttpLoggingInterceptor para trazabilidad en logcat",
                "Garantizar patrón Singleton para evitar crear múltiples instancias de Retrofit"
            ),
            formalAnalysis = """
                Retrofit es un cliente HTTP tipo-seguro desarrollado por Square.
                Transforma una interfaz abstracta de Kotlin en una implementación dinámica en tiempo de ejecución mediante Dynamic Proxies.
                
                Componentes Clave:
                • Interfaz de Servicio: Define los métodos HTTP y sus parámetros (@GET, @POST, @Path, @Query).
                • Retrofit.Builder: Ensambla el cliente HTTP, asigna la Base URL y conecta el analizador JSON.
                • Conversor (Moshi / Gson): Recibe el flujo de bytes (ResponseBody) y lo parsea automáticamente a data classes.
                • OkHttp Client: Gestiona la conexión TCP, TLS handshake, reintentos y caché HTTP.
            """.trimIndent(),
            codeSample = """
                // RetrofitClient.kt
                object RetrofitClient {
                    private const val BASE_URL = "https://api.openweathermap.org/"

                    private val okHttpClient = OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()

                    val weatherService: OpenWeatherApiService by lazy {
                        Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(MoshiConverterFactory.create())
                            .build()
                            .create(OpenWeatherApiService::class.java)
                    }
                }
            """.trimIndent()
        ),
        AbcPhaseInfo(
            phaseNumber = 3,
            title = "ANÁLISIS",
            durationMinutes = 5,
            summary = "Modelado riguroso del JSON devuelto por OpenWeatherMap y diseño de la estrategia de errores.",
            coreQuestion = "¿Qué estructura tiene el JSON de respuesta y cómo capturamos y gestionamos fallos de red?",
            keyConcepts = listOf(
                "Mapeo de DTO: @Json(name=\"temp\") o @SerializedName",
                "Nulabilidad segura: Todos los campos del JSON deben ser seguros a nulos",
                "Manejo de códigos HTTP: 200 (OK), 401 (Unauthorized), 404 (Not Found)",
                "Excepciones de transporte: UnknownHostException, SocketTimeoutException",
                "Patrón Result<T>: Encapsular éxito o error antes de llegar a la UI"
            ),
            checklist = listOf(
                "Identificar objeto raíz y objetos anidados ('main', 'weather', 'wind')",
                "Verificar que la lista 'weather' es un arreglo JSON ('[ {...} ]')",
                "Manejar temperaturas en Kelvin por defecto y forzar 'units=metric'",
                "Implementar bloque try-catch para capturar HttpException e IOException",
                "Proveer mensajes amigables al usuario final ante pérdida de señal"
            ),
            formalAnalysis = """
                El JSON de OpenWeatherMap contiene objetos anidados:
                - `main`: temp, feels_like, temp_min, temp_max, humidity, pressure.
                - `weather`: array con id, main, description, icon.
                - `wind`: speed, deg.
                
                Si el nombre de la variable en Kotlin difiere de la clave JSON, usamos `@Json(name = "feels_like")`.
                
                Estrategia de Errores:
                1. 401 Unauthorized: La API key es incorrecta o no ha sido activada en OpenWeatherMap.
                2. 404 Not Found: La ciudad especificada no existe en la base de datos geográfica.
                3. IOException: El dispositivo está en modo avión o sin internet.
            """.trimIndent(),
            codeSample = """
                // WeatherDto.kt
                @JsonClass(generateAdapter = true)
                data class WeatherResponse(
                    @Json(name = "name") val name: String?,
                    @Json(name = "main") val main: MainMetrics?,
                    @Json(name = "weather") val weather: List<WeatherDescription>?,
                    @Json(name = "wind") val wind: WindMetrics?
                )

                @JsonClass(generateAdapter = true)
                data class MainMetrics(
                    @Json(name = "temp") val temp: Double,
                    @Json(name = "humidity") val humidity: Int?,
                    @Json(name = "feels_like") val feelsLike: Double?
                )
            """.trimIndent()
        ),
        AbcPhaseInfo(
            phaseNumber = 4,
            title = "SOLUCIÓN",
            durationMinutes = 10,
            summary = "Construcción operativa de la interface con @GET, repositorio desacoplado y ejecución de llamada real.",
            coreQuestion = "¿Cómo unimos la interfaz, el repositorio y el ViewModel para consultar el clima real?",
            keyConcepts = listOf(
                "Interface con anotación @GET(\"data/2.5/weather\")",
                "Parámetros @Query: q, appid, units, lang",
                "WeatherRepository con despacho en Dispatchers.IO",
                "Transformación de DTO de red a Modelo de Dominio limpio",
                "Observabilidad en Compose mediante StateFlow"
            ),
            checklist = listOf(
                "Declarar función 'suspend fun getWeather(@Query(\"q\") city: String...)'",
                "Configurar parámetros por defecto: units = 'metric', lang = 'es'",
                "Construir método en repositorio que retorne Result<WeatherReport>",
                "Conectar evento de búsqueda en UI con corrutina viewModelScope.launch",
                "Probar llamada básica con respuesta 200 OK y visualización de headers"
            ),
            formalAnalysis = """
                En esta fase culminante de desarrollo, el equipo programa la interface Retrofit definitiva.
                La llamada básica se ejecuta en un hilo secundario (IO) para no congelar el hilo principal (Main Thread) de la interfaz de usuario.
                
                Se crea la capa de Repositorio para que la interfaz Compose jamás conozca los detalles técnicos de Retrofit, respetando los principios de Clean Architecture y separación de responsabilidades.
            """.trimIndent(),
            codeSample = """
                // OpenWeatherApiService.kt
                interface OpenWeatherApiService {
                    @GET("data/2.5/weather")
                    suspend fun getCurrentWeather(
                        @Query("q") cityName: String,
                        @Query("appid") apiKey: String,
                        @Query("units") units: String = "metric",
                        @Query("lang") lang: String = "es"
                    ): WeatherResponse
                }
            """.trimIndent()
        ),
        AbcPhaseInfo(
            phaseNumber = 5,
            title = "CONCLUSIONES",
            durationMinutes = 5,
            summary = "Validación de resultados: datos reales en pantalla, satisfacción de usuarios y métricas de mejora.",
            coreQuestion = "¿Qué impacto tuvo el paso de datos hardcodeados a Retrofit y qué aprendió el equipo?",
            keyConcepts = listOf(
                "Datos Vivos: Clima exacto y variable por geolocalización",
                "Eliminación de Quejas: Las valoraciones suben de 1.2★ a 4.9★",
                "Mantenibilidad: Arquitectura modular y extensible",
                "Resiliencia: Manejo elegante de fallas de red con reintentos",
                "Documentación y Entrega del Caso ABC"
            ),
            checklist = listOf(
                "Demostrar que ciudades distintas muestran temperaturas y climas distintos",
                "Verificar que la fecha y hora de actualización reflejan la realidad",
                "Comprobar que ante error de red el usuario recibe un aviso comprensible",
                "Generar el reporte grupal del caso con roles y aprendizajes",
                "Completar la rúbrica de evaluación ABC"
            ),
            formalAnalysis = """
                Al conectar Retrofit:
                1. La aplicación pasa de ser un cascarón estático e inútil a un producto de producción confiable.
                2. Los usuarios reciben información meteorológica verídica y oportuna.
                3. El equipo domina el estándar de la industria en Android para consumo de APIs REST.
                4. La arquitectura desacoplada permite cambiar de proveedor de clima sin alterar los Composables.
            """.trimIndent(),
            codeSample = """
                // Conclusión del Caso:
                // Antes:
                // val temp = 22.0 // ¡Datos falsos para siempre!
                //
                // Ahora con Retrofit:
                // val weather = repository.getWeather(city).getOrThrow()
                // Text("${"$"}{weather.temperature}°C - ${"$"}{weather.condition}")
            """.trimIndent()
        )
    )
}
