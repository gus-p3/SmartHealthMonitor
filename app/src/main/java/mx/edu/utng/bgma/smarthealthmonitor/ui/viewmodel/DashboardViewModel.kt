package mx.edu.utng.bgma.smarthealthmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.bgma.smarthealthmonitor.data.models.MockData

class DashboardViewModel : ViewModel() {

    // FC: viene del wearable real vía Repository.
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) MockData.fcActual else it }
        .stateIn(
            scope          = viewModelScope,
            started        = SharingStarted.WhileSubscribed(5_000),
            initialValue   = MockData.fcActual
        )

    // Pasos: viene del repositorio
    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .map { if (it == 0) MockData.pasosActual else it }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = MockData.pasosActual
        )

    // SpO2: CORREGIDO el nombre a spo2Flow (coincidiendo con el Repositorio)
    val spO2: StateFlow<Int> = SmartHealthRepository.spo2Flow
        .map { if (it == 0) 98 else it }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 98
        )

    // HISTORIAL: Ahora lee de la base de datos Room vía Repositorio
    val historial: StateFlow<List<LecturaFC>> = SmartHealthRepository.obtenerHistorial()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}