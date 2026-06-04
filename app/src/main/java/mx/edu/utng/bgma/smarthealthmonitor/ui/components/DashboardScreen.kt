package mx.edu.utng.bgma.smarthealthmonitor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
// Import importante para el botón de simulación
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import mx.edu.utng.bgma.smarthealthmonitor.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    onAlertClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel(),
    // Parámetros opcionales para facilitar la Preview
    fcManual: Int? = null,
    pasosManual: Int? = null,
    spO2Manual: Int? = null
) {
    // Scope para ejecutar funciones suspendidas (como la simulación)
    val scope = rememberCoroutineScope()
    
    // Observar estados del ViewModel
    val fcState by viewModel.fc.collectAsState()
    val pasosState by viewModel.pasos.collectAsState()
    val spO2State by viewModel.spO2.collectAsState()
    val historial by viewModel.historial.collectAsState()

    // Usar valor manual (si existe) o el del estado (reactivo)
    val fc = fcManual ?: fcState
    val pasos = pasosManual ?: pasosState
    val spO2 = spO2Manual ?: spO2State

    SmartHealthMonitorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SmartHealth",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor    = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick       = onAlertClick,
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector       = Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint              = MaterialTheme.colorScheme.onError
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding        = PaddingValues(16.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                // ── Tarjeta FC ──
                item {
                    TarjetaDato(
                        valor      = "$fc",
                        unidad     = "bpm",
                        label      = "Frecuencia cardíaca",
                        colorValor = MaterialTheme.colorScheme.error,
                        esNormal   = fc in 60..100
                    )
                }

                // ── Tarjeta Pasos ──
                item {
                    TarjetaDato(
                        valor      = "%,d".format(pasos),
                        unidad     = "pasos",
                        label      = "Pasos del día",
                        colorValor = MaterialTheme.colorScheme.primary
                    )
                }

                // ── Tarjeta SpO2 ──
                item {
                    TarjetaDato(
                        valor      = "$spO2",
                        unidad     = "%",
                        label      = "Saturación de Oxígeno",
                        colorValor = MaterialTheme.colorScheme.tertiary,
                        esNormal   = spO2 >= 95
                    )
                }

                // ── Encabezado Historial ──
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Historial reciente",
                            style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = onHistorialClick) {
                            Text("Ver todo")
                        }
                    }
                }

                // ── Lista del historial ──
                items(historial, key = { it.id }) { lectura ->
                    FilaHistorial(lectura = lectura)
                }

                // ── Botón de Simulación (Solo visible en modo Debug) ──
                item {
                    val isDebug = true
                    if (isDebug) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                // Las funciones suspendidas deben llamarse dentro de un scope
                                scope.launch {
                                    SmartHealthRepository.actualizarFC((60..120).random())
                                    SmartHealthRepository.actualizarPasos((1000..9000).random())
                                    SmartHealthRepository.actualizarSpO2((90..100).random())
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simular Wearable (Debug)")
                        }
                    }
                }
            }
        }
    }
}

// Preview corregida para mostrar datos sin fallar por el ViewModel
@Preview(showBackground = true, name = "Dashboard Completo",
    showSystemUi = true, device = "id:pixel_6")
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen(
        fcManual = 75,
        pasosManual = 4500,
        spO2Manual = 98
    )
}
