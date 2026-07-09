package mx.utng.bgma.smarthealthmonitor.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC


class TvViewModel : ViewModel() {

    // FC actual del wearable (o 0 si no hay dato)
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5_000), 0)

    // Historial de lecturas desde Room DAO
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList())
}


