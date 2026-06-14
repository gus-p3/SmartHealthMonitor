package mx.edu.utng.bgma.smarthealthmonitor.wear.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC

// presentation/WearDashboardViewModel.kt
class WearDashboardViewModel : ViewModel() {

    // Reutiliza el mismo Repository del módulo app
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) 72 else it }  // valor por defecto
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000), 72)

    // Reto adicional: Flujo de pasos
    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .stateIn(viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000), 0)
    // ← NUEVO: historial desde Room
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList())


}