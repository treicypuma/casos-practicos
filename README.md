# Caso Práctico: Metodología ABC - App de Clima con Retrofit

Aplicación móvil desarrollada en **Android con Jetpack Compose y Kotlin**, resolviendo el caso de estudio bajo la metodología **ABC (Aprendizaje Basado en Casos)**:

> **CASO:** *"Una app de clima muestra datos hardcodeados. Los usuarios se quejan. Deciden conectar a OpenWeatherMap API pero nadie sabe usar Retrofit."*

---

## 📋 Estructura de las 5 Fases del Caso ABC

1. **1️⃣ LECTURA (5 min):** 
   - Identificación de necesidades: Base URL (`https://api.openweathermap.org/`), Endpoint (`/data/2.5/weather`), permisos en `AndroidManifest.xml` (`INTERNET`), conversor JSON (`Moshi / Gson`).
   - Checklist interactivo de requerimientos de infraestructura.
2. **2️⃣ INFORMACIÓN (5 min):** 
   - Estudio de la arquitectura de Retrofit: Interface abstracta, `Retrofit.Builder`, adaptadores y cliente OkHttp con `HttpLoggingInterceptor`.
3. **3️⃣ ANÁLISIS (5 min):** 
   - Modelado de Data Classes para el JSON de OpenWeatherMap (`WeatherResponse`, `MainMetrics`, etc.).
   - Estrategia de manejo de errores (HTTP 401, 404, pérdida de conexión y fallos de transporte).
4. **4️⃣ SOLUCIÓN (10 min):** 
   - Construcción de `OpenWeatherApiService` con `@GET` y parámetros `@Query`.
   - Implementación de `WeatherRepository` con Coroutines (`Dispatchers.IO`) y `Result<T>`.
   - Sandbox interactivo para ejecutar la llamada básica y ver latencia en ms y código HTTP.
5. **5️⃣ CONCLUSIONES (5 min):** 
   - Aplicación de Clima funcional con selector en vivo: **Modo Hardcodeado (22°C estático)** vs **Modo Retrofit Live API (Tiempo real)**.
   - Generador y exportador del informe final del caso.

---

## 🛠️ Tecnologías y Librerías Utilizadas

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material Design 3 - Tema Clásico y Formal)
- **Networking:** Retrofit 2 (`2.12.0`)
- **HTTP Client:** OkHttp 4 (`4.10.0`) + Logging Interceptor
- **JSON Converter:** Moshi Kotlin (`1.15.2`) + Moshi Converter
- **Asincronía:** Kotlin Coroutines & StateFlow
- **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Testing:** Unit Tests & Robolectric

---

## 🚀 Cómo Ejecutar en Android Studio

1. Clona o extrae el repositorio en tu computadora:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   ```
2. Abre **Android Studio** (Koala o superior recomendado).
3. Selecciona **Open** y elige la carpeta raíz del proyecto.
4. Espera a que Gradle sincronice las dependencias automáticamente.
5. Conecta tu dispositivo o inicia un emulador y presiona **Run (`Shift + F10`)**.

---

## 📦 APK Compilado

El instalador compilado se ubica en:
```
app/build/outputs/apk/debug/app-debug.apk
```
