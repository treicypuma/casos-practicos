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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AbcCaseData
import com.example.ui.components.CodeBlockCard
import com.example.ui.components.ComplaintItemCard
import com.example.ui.components.FormalHeader
import com.example.ui.components.PhaseStepIndicator
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold600
import com.example.ui.theme.Navy700
import com.example.ui.theme.Ruby500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.WeatherAbcUiState
import com.example.ui.viewmodel.WeatherAbcViewModel

@Composable
fun AbcCaseScreen(
    state: WeatherAbcUiState,
    viewModel: WeatherAbcViewModel,
    modifier: Modifier = Modifier
) {
    val currentPhase = AbcCaseData.phases.firstOrNull { it.phaseNumber == state.currentPhaseNumber }
        ?: AbcCaseData.phases.first()

    var showComplaints by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FormalHeader(
                title = "Metodología ABC",
                subtitle = "Aprendizaje Basado en Casos • Sesión Grupal (30 min)",
                badgeText = "FASE ${currentPhase.phaseNumber}/5",
                badgeColor = Gold600
            )

            // Tarjeta del Caso
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("case_context_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "CASO DE ESTUDIO",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Gold600
                        )
                        Surface(
                            color = Ruby500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Alta Prioridad",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Ruby500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "«Una app de clima muestra datos hardcodeados. Los usuarios se quejan. Deciden conectar a OpenWeatherMap API pero nadie sabe usar Retrofit.»",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showComplaints = !showComplaints },
                            modifier = Modifier.testTag("toggle_complaints_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Ruby500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showComplaints) "Ocultar Quejas" else "Ver Quejas (${AbcCaseData.complaints.size})",
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.setTab(AppNavTab.WEATHER_APP) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                            modifier = Modifier.testTag("view_broken_app_btn")
                        ) {
                            Text("Ver App Actual", fontSize = 12.sp)
                        }
                    }

                    AnimatedVisibility(visible = showComplaints) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Quejas reales en Play Store:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AbcCaseData.complaints.forEach { complaint ->
                                ComplaintItemCard(complaint = complaint)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Indicador de Fases
            PhaseStepIndicator(
                currentPhase = state.currentPhaseNumber,
                completedPhases = state.completedPhases,
                onSelectPhase = { viewModel.setPhase(it) }
            )
        }

        item {
            // Detalle de la Fase Actual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phase_detail_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${currentPhase.phaseNumber}️⃣ ${currentPhase.title}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = Gold500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${currentPhase.durationMinutes} MINUTOS",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Gold600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentPhase.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = Navy700.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Navy700,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "PREGUNTA CLAVE DE LA FASE:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Navy700
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentPhase.coreQuestion,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CONCEPTOS CLAVE:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    currentPhase.keyConcepts.forEach { concept ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Gold600, shape = RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = concept,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ANÁLISIS TÉCNICO FORMAL:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentPhase.formalAnalysis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    CodeBlockCard(
                        title = "Implementación Fase ${currentPhase.phaseNumber}",
                        code = currentPhase.codeSample
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CHECKLIST DE VERIFICACIÓN EN GRUPO:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    currentPhase.checklist.forEach { itemText ->
                        val isChecked = state.checkedItems.contains(itemText)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleChecklistItem(itemText) }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { viewModel.toggleChecklistItem(itemText) },
                                colors = CheckboxDefaults.colors(checkedColor = Emerald500),
                                modifier = Modifier.testTag("chk_${itemText.take(10)}")
                            )
                            Text(
                                text = itemText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isChecked) Emerald600 else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // Notas del equipo
                    val currentNote = state.phaseNotes[currentPhase.phaseNumber] ?: ""
                    OutlinedTextField(
                        value = currentNote,
                        onValueChange = { viewModel.updatePhaseNote(currentPhase.phaseNumber, it) },
                        label = { Text("Conclusiones y acuerdos del grupo para esta fase") },
                        placeholder = { Text("Escriban aquí sus hallazgos grupales...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phase_notes_input"),
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setTimerToPhaseMinutes(currentPhase.durationMinutes) },
                            modifier = Modifier.testTag("start_phase_timer_btn")
                        ) {
                            Text("Temporizador ${currentPhase.durationMinutes}m", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.markPhaseCompleted(currentPhase.phaseNumber) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            modifier = Modifier.testTag("complete_phase_btn")
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aprobar Fase", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            // Acciones Rápidas del Caso
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACCIONES DE LABORATORIO EN VIVO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Gold400
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setTab(AppNavTab.WEATHER_APP) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                            modifier = Modifier.weight(1f).testTag("action_go_app_btn")
                        ) {
                            Text("Probar App Clima", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.setTab(AppNavTab.CODE_INSPECTOR) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            modifier = Modifier.weight(1f).testTag("action_inspect_code_btn")
                        ) {
                            Text("Inspeccionar Red", fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
