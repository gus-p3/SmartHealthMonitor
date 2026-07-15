package mx.utng.bgma.smarthealthmonitor.tv.domain.model
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC
 
data class TvUiState(
    val lecturas    : List<LecturaFC> = emptyList(),
    val estadisticas: List<LecturaFC> = emptyList(),
    val fcActual    : Int             = 0,
    val fcEstado    : String          = "Normal",
    val ultimaHora  : String          = "--:--:--",
    val isLoading   : Boolean         = true,
    val error       : String?         = null,
)
