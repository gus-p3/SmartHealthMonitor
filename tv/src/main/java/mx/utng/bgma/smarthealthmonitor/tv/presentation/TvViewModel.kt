package mx.utng.bgma.smarthealthmonitor.tv.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository
import mx.edu.utng.bgma.smarthealthmonitor.mqtt.TvMessage
import mx.utng.bgma.smarthealthmonitor.tv.domain.model.TvUiState
import mx.utng.bgma.smarthealthmonitor.tv.mqtt.MqttTvSubscriber

class TvViewModel(
    private val repository: SmartHealthRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<TvMessage?>(null)
    private val mqttSubscriber = MqttTvSubscriber(context, mqttFlow)

    init {
        mqttSubscriber.connect()

        // Observar historial reactivo del Room DAO
        viewModelScope.launch {
            repository.obtenerHistorial()
                .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                .collect { lecturas ->
                    _state.update { it.copy(lecturas = lecturas, isLoading = false) }
                }
        }

        // Observar mensajes MQTT y actualizar el estado de la UI
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                tvMsg ?: return@collect
                
                // 1. Actualizar el repositorio local para persistir el dato en la BD de la TV
                repository.actualizarFC(tvMsg.bpm)

                // 2. Actualizar el estado de la UI
                _state.update { it.copy(
                    fcActual = tvMsg.bpm,
                    fcEstado = tvMsg.estado,
                    ultimaHora = tvMsg.hora,
                    isLoading = false
                )}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttSubscriber.disconnect()
    }
}
