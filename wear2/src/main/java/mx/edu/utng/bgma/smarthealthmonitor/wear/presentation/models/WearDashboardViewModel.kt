package mx.edu.utng.bgma.smarthealthmonitor.wear.presentation.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.bgma.smarthealthmonitor.wear.mqtt.MqttWearPublisher

// presentation/WearDashboardViewModel.kt
class WearDashboardViewModel(application: Application) : AndroidViewModel(application) {

    // Publisher MQTT — usa el contexto de la aplicación
    private val mqttPublisher = MqttWearPublisher(application)

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

    // ← historial desde Room
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList())

    init {
        // Conectar al broker MQTT al iniciar el ViewModel
        mqttPublisher.connect()

        // Publicar FC vía MQTT cada vez que cambia el valor
        viewModelScope.launch {
            SmartHealthRepository.fcFlow.collect { bpm ->
                val estado = when {
                    bpm < 60  -> "FC Baja"
                    bpm > 100 -> "FC Alta"
                    else      -> "Normal"
                }
                mqttPublisher.publishFC(bpm, estado)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttPublisher.disconnect()
    }
}