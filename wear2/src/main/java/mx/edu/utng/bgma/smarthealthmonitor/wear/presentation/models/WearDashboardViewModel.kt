package mx.edu.utng.bgma.smarthealthmonitor.wear.presentation.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.bgma.smarthealthmonitor.wear.mqtt.MqttWearPublisher

class WearDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val mqttPublisher = MqttWearPublisher(application)
    private val neonRepo = mx.edu.utng.bgma.smarthealthmonitor.wear.data.WearNeonRepository()

    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) 72 else it }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000), 72)

    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .stateIn(viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000), 0)

    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList())

    init {
        // Conectar al broker MQTT al iniciar
        mqttPublisher.connect()
    }

    /**
     * Publica manualmente la FC vía MQTT.
     * De esta forma, solo se envía cuando el usuario pulsa el botón
     * y no automáticamente con cada cambio del sensor del emulador.
     */
    fun publicarManualMqtt(bpm: Int) {
        val estado = when {
            bpm < 60  -> "FC Baja"
            bpm > 100 -> "FC Alta"
            else      -> "Normal"
        }
        mqttPublisher.publishFC(bpm, estado)
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            runCatching { neonRepo.publicarLectura(bpm, estado) }
                .onFailure { android.util.Log.w("WEAR","Error Neon: ${it.message}") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttPublisher.disconnect()
    }
}