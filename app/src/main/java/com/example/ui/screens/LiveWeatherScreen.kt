package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FormalHeader
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold600
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.Ruby500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.ApiProvider
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.WeatherAbcUiState
import com.example.ui.viewmodel.WeatherAbcViewModel

@Composable
fun LiveWeatherScreen(
    state: WeatherAbcUiState,
    viewModel: WeatherAbcViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(state.selectedCity) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    val popularCities = listOf("Madrid", "Lima", "Buenos Aires", "Ciudad de México", "Bogotá", "Tokio", "Londres", "Nueva York")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FormalHeader(
                title = "Aplicación del Clima",
                subtitle = "Comparativa en Vivo: Datos Hardcodeados vs Retrofit API Real",
                badgeText = if (state.isHardcodedMode) "MODO HARDCODEADO" else "RETROFIT CONECTADO",
                badgeColor = if (state.isHardcodedMode) Ruby500 else Emerald500
            )

            // Selector Central de Modo (Hardcodeado vs Retrofit)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mode_switch_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isHardcodedMode) Ruby500.copy(alpha = 0.08f) else Emerald500.copy(alpha = 0.08f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (state.isHardcodedMode) Ruby500 else Emerald500
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (state.isHardcodedMode) "⚠️ MODO HARDCODEADO (EL PROBLEMA)" else "⚡ MODO RETROFIT API (LA SOLUCIÓN)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (state.isHardcodedMode) Ruby500 else Emerald600
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (state.isHardcodedMode)
                                "Datos fijos (22°C). Los usuarios se quejan de falta de datos reales."
                            else
                                "Conectado a servidor real vía HTTP GET con deserialización automática.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = !state.isHardcodedMode,
                        onCheckedChange = { isRetrofit ->
                            viewModel.toggleHardcodedMode(!isRetrofit)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Emerald500,
                            checkedTrackColor = Emerald500.copy(alpha = 0.3f),
                            uncheckedThumbColor = Ruby500,
                            uncheckedTrackColor = Ruby500.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("mode_toggle_switch")
                    )
                }
            }
        }

        item {
            // Buscador de Ciudades y Botón de Ajustes API
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar Ciudad") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Slate400)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.setCity(searchQuery) },
                            modifier = Modifier.testTag("search_city_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Buscar", tint = Navy700)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("city_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                IconButton(
                    onClick = { showApiKeyDialog = true },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, Slate700.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .testTag("api_key_settings_btn")
                ) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = "Configurar API Key", tint = Gold600)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            // Ciudades Rápidas
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(popularCities) { city ->
                    val isSelected = state.selectedCity.equals(city, ignoreCase = true)
                    Surface(
                        color = if (isSelected) Navy700 else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .clickable {
                                searchQuery = city
                                viewModel.setCity(city)
                            }
                            .testTag("city_chip_$city")
                    ) {
                        Text(
                            text = city,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            // Tarjeta Principal del Clima
            val weather = state.currentWeather
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate900),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Gold400)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Consultando Retrofit HTTP GET...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (state.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Ruby500.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Ruby500))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Ruby500, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Error en Petición Retrofit",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Ruby500
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadWeather() },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy700)
                        ) {
                            Text("Reintentar Llamada")
                        }
                    }
                }
            } else if (weather != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weather_display_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (weather.isRealApi) Navy900 else Slate800
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = weather.cityName.uppercase(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = weather.country,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Gold400
                                )
                            }

                            Surface(
                                color = if (weather.isRealApi) Emerald500.copy(alpha = 0.2f) else Ruby500.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (weather.isRealApi) "DATOS EN VIVO" else "HARDCODED",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (weather.isRealApi) Emerald500 else Ruby500
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${String.format("%.1f", weather.temperature)}°C",
                                    fontSize = 52.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.testTag("weather_temp_text")
                                )
                                Text(
                                    text = weather.condition,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Slate200
                                )
                                Text(
                                    text = "Sensación: ${String.format("%.1f", weather.feelsLike)}°C",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate400
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Gold600.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (weather.condition.contains("Lluvia", ignoreCase = true)) Icons.Default.WaterDrop else Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Slate700)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Métricas en rejilla formal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherMetricItem(
                                icon = Icons.Default.WaterDrop,
                                label = "Humedad",
                                value = "${weather.humidity}%"
                            )
                            WeatherMetricItem(
                                icon = Icons.Default.Air,
                                label = "Viento",
                                value = "${String.format("%.1f", weather.windSpeed)} km/h"
                            )
                            WeatherMetricItem(
                                icon = Icons.Default.Speed,
                                label = "Presión",
                                value = "${weather.pressure} hPa"
                            )
                            WeatherMetricItem(
                                icon = Icons.Default.Cloud,
                                label = "Nubes",
                                value = "${weather.clouds}%"
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Fuente: ${weather.sourceLabel} • ${weather.fetchedAt}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                    }
                }
            }
        }

        item {
            // Pronóstico por Horas
            state.currentWeather?.let { weather ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PRONÓSTICO POR HORAS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate400
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(weather.hourlyForecast) { item ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(text = item.hour, style = MaterialTheme.typography.labelSmall, color = Slate400)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${String.format("%.0f", item.temp)}°",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = item.condition, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Enlace al inspector de Red
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTab(AppNavTab.CODE_INSPECTOR) }
                    .testTag("inspect_traffic_shortcut"),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "MONITOR DE LLAMADAS HTTP (OKHTTP)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Gold400
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.trafficLogs.size} peticiones interceptadas en esta sesión.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200
                        )
                    }

                    Surface(
                        color = Navy700,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Inspeccionar Logs",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showApiKeyDialog) {
        var tempKey by remember { mutableStateOf(state.openWeatherApiKey) }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = {
                Text(
                    text = "Configuración OpenWeatherMap",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "El caso de estudio solicita conectar a OpenWeatherMap API (/data/2.5/weather).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Si ya tiene una API Key de OpenWeatherMap, ingrésela abajo.\n• Si no tiene una clave activa (tardan hasta 2h en registrarse en el sitio web), la app utiliza automáticamente el motor abierto Retrofit de respaldo para datos 100% reales.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        label = { Text("OpenWeatherMap API Key (Opcional)") },
                        placeholder = { Text("Ej: 4a2b3c... (32 caracteres)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("api_key_dialog_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setOpenWeatherApiKey(tempKey)
                        if (tempKey.isNotBlank()) {
                            viewModel.setApiProvider(ApiProvider.OPEN_WEATHER_MAP)
                        }
                        showApiKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy700)
                ) {
                    Text("Guardar y Probar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun WeatherMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = Gold400, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Slate400, fontSize = 10.sp)
        Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
    }
}
