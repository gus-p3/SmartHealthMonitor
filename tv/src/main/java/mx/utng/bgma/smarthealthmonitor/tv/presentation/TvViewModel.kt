package mx.utng.bgma.smarthealthmonitor.tv.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC
import mx.edu.utng.bgma.smarthealthmonitor.data.remote.LecturaFcDto
import mx.utng.bgma.smarthealthmonitor.tv.data.TvNeonRepository
import mx.utng.bgma.smarthealthmonitor.tv.domain.model.TvUiState

class TvViewModel(private val context: Context) : ViewModel() {
    private val neonRepo = TvNeonRepository()
    private val _state   = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()
 
    private val mqttFlow = MutableStateFlow<mx.edu.utng.bgma.smarthealthmonitor.mqtt.TvMessage?>(null)
    private val mqttSubscriber = mx.utng.bgma.smarthealthmonitor.tv.mqtt.MqttTvSubscriber(context, mqttFlow)

    init { 
        cargarDatos() 
        mqttSubscriber.connect()

        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                if (tvMsg != null) {
                    _state.update { it.copy(
                        fcActual = tvMsg.bpm,
                        fcEstado = tvMsg.estado,
                        ultimaHora = tvMsg.hora
                    )}
                    // Cuando llega un mensaje nuevo, refrescamos el historial de Neon
                    cargarDatos()
                }
            }
        }
    }
 
    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading=true) }
            try {
                val lecturas = neonRepo.obtenerHistorialCompleto(50)
                val stats    = neonRepo.obtenerEstadisticas()
                _state.update { it.copy(
                    lecturas  = lecturas.map { dto -> dto.toLecturaFC() },
                    estadisticas = stats.map { dto -> dto.toLecturaFC() },
                    isLoading = false
                )}
            } catch (e: Exception) {
                _state.update { it.copy(error=e.message, isLoading=false) }
            }
        }
    }
    fun refresh() = cargarDatos()

    override fun onCleared() {
        super.onCleared()
        mqttSubscriber.disconnect()
    }
}

fun LecturaFcDto.toLecturaFC() = LecturaFC(
    id = this.id,
    bpm = this.bpm,
    estado = this.estado,
    dispositivo = this.dispositivo,
    hora = this.hora,
    sincronizado = true
)
