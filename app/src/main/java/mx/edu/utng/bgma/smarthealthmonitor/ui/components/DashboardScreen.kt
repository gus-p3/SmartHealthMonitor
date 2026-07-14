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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import mx.edu.utng.bgma.smarthealthmonitor.ui.screens.AlertaScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel(),
) {
    // Scope para ejecutar funciones suspendidas (como la simulación)
    val scope = rememberCoroutineScope()
    
    // Observar estados del ViewModel
    val fc       by viewModel.fc.collectAsState()
    val pasos    by viewModel.pasos.collectAsState()
    val spO2 by viewModel.spO2.collectAsState()
    val historial by viewModel.historial.collectAsState()

    // ── Estado del diálogo y Snackbar ──────────────────────
    var mostrarAlerta by remember { mutableStateOf(false) }
    val snackbarHost  = remember { SnackbarHostState() }

    // ── Diálogo condicional ────────────────────────────────
    if (mostrarAlerta) {
        AlertaScreen(
            fc          = fc,
            onDismiss   = { mostrarAlerta = false },
            onConfirmar = { nota -> // S9: Recibe la nota de la alerta
                mostrarAlerta = false
                scope.launch {
                    // S10: Snackbar con acción de Deshacer
                    val result = snackbarHost.showSnackbar(
                        message     = "✅ Alerta enviada. Nota: $nota",
                        actionLabel = "Deshacer",
                        duration    = SnackbarDuration.Long
                    )
                    
                    // Si el usuario presiona "Deshacer", mostramos un segundo snackbar
                    if (result == SnackbarResult.ActionPerformed) {
                        snackbarHost.showSnackbar(
                            message  = "Alerta cancelada",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        )
    }

    SmartHealthMonitorTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHost) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SmartHealth",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        AndroidView(
                            factory = { context ->
                                val themedContext = android.view.ContextThemeWrapper(
                                    context,
                                    mx.edu.utng.bgma.smarthealthmonitor.R.style.Theme_CastCompat
                                )
                                MediaRouteButton(themedContext).apply {
                                    CastButtonFactory.setUpMediaRouteButton(themedContext, this)
                                }
                            },
                            modifier = Modifier.size(48.dp)
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
                    onClick        = { mostrarAlerta = true },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint = MaterialTheme.colorScheme.onError)
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
    )
}
