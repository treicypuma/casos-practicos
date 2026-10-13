package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
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
import com.example.ui.viewmodel.WeatherAbcUiState
import com.example.ui.viewmodel.WeatherAbcViewModel

@Composable
fun TimerReportScreen(
    state: WeatherAbcUiState,
    viewModel: WeatherAbcViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val minutes = state.timerSecondsLeft / 60
    val seconds = state.timerSecondsLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val totalSeconds = 30 * 60
    val progress = (totalSeconds - state.timerSecondsLeft).toFloat() / totalSeconds.toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FormalHeader(
                title = "Taller Grupal (30 min)",
                subtitle = "Control de Tiempos por Fase, Roles de Equipo e Informe Final",
                badgeText = if (state.isTimerActive) "EN MARCHA" else "EN PAUSA",
                badgeColor = if (state.isTimerActive) Emerald500 else Gold600
            )

            // Reloj Principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timer_master_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TIEMPO RESTANTE DEL TALLER ABC",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Gold400
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        modifier = Modifier.testTag("timer_countdown_text")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Gold400,
                        trackColor = Slate800
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.toggleTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isTimerActive) Ruby500 else Emerald600
                            ),
                            modifier = Modifier.testTag("toggle_timer_btn")
                        ) {
                            Icon(
                                imageVector = if (state.isTimerActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (state.isTimerActive) "Pausar" else "Iniciar")
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier.testTag("reset_timer_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("30 min")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    // Desglose de 5 Fases
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "1. Lectura (5m)", fontSize = 10.sp, color = Slate400)
                        Text(text = "2. Info (5m)", fontSize = 10.sp, color = Slate400)
                        Text(text = "3. Análisis (5m)", fontSize = 10.sp, color = Slate400)
                        Text(text = "4. Solución (10m)", fontSize = 10.sp, color = Gold400, fontWeight = FontWeight.Bold)
                        Text(text = "5. Conclusión (5m)", fontSize = 10.sp, color = Slate400)
                    }
                }
            }
        }

        item {
            // Comparativa de Impacto: Antes vs Después
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "IMPACTO DE LA MIGRACIÓN RETROFIT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Antes
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Ruby500.copy(alpha = 0.08f)),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "ANTES (HARDCODED)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ruby500)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "⭐ 1.2 / 5.0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ruby500)
                                Text(text = "• Temperatura estática 22°C\n• Quejas de usuarios\n• Sin conexión a API\n• Código rígido", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Después
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Emerald500.copy(alpha = 0.08f)),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "DESPUÉS (RETROFIT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald600)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "⭐ 4.9 / 5.0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald600)
                                Text(text = "• Clima en tiempo real\n• Ciudades mundiales\n• Manejo 401/404/Red\n• Clean Architecture", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Roles de Equipo para Trabajo Grupal
            Text(
                text = "ROLES DEL EQUIPO (TRABAJO GRUPAL)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate400
            )
            Spacer(modifier = Modifier.height(4.dp))
            viewModel.teamRoles.forEachIndexed { index, role ->
                val isSelected = state.currentRoleIndex == index
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { viewModel.selectRole(index) }
                        .testTag("role_card_$index"),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Navy900 else MaterialTheme.colorScheme.surface
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Gold400 else Slate800),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = if (isSelected) Slate900 else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${role.roleTitle} (${role.memberName})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = role.responsibilities,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) Slate200 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Enfoque: ${role.phaseFocus}",
                                fontSize = 10.sp,
                                color = if (isSelected) Gold400 else Slate400
                            )
                        }
                    }
                }
            }
        }

        item {
            // Generador de Informe Final del Caso ABC
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("final_report_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INFORME FINAL DEL CASO ABC",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate400
                        )
                        IconButton(
                            onClick = {
                                val reportText = viewModel.generateFinalReportMarkdown()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Informe ABC Clima Retrofit", reportText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Informe copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_report_btn")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar Informe", tint = Navy700)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Resumen académico formal estructurado con las 5 fases, roles y conclusiones alcanzadas por el grupo de trabajo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate900)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = viewModel.generateFinalReportMarkdown().take(400) + "...\n[Presione el botón copiar para obtener el reporte completo]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Slate200,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
