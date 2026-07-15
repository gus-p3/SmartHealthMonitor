package mx.utng.bgma.smarthealthmonitor.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.edu.utng.bgma.smarthealthmonitor.data.remote.LecturaFcDto
import mx.edu.utng.bgma.smarthealthmonitor.data.remote.NeonClient
import mx.edu.utng.bgma.smarthealthmonitor.data.remote.NeonRequest
 
class TvNeonRepository {
 
    /** Obtener historial completo de los 3 dispositivos */
    suspend fun obtenerHistorialCompleto(limite: Int = 50): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT id,bpm,estado,dispositivo,hora,created_at
                               FROM lecturas_fc
                               ORDER BY created_at DESC
                               LIMIT $1""".trimIndent(),
                    params = listOf(limite)
                )
            ).rows
        }
 
    /** Estadísticas por dispositivo */
    suspend fun obtenerEstadisticas(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT dispositivo,
                               ROUND(AVG(bpm)) AS bpm,
                               'Promedio' AS estado,
                               MAX(hora) AS hora
                               FROM lecturas_fc
                               GROUP BY dispositivo""".trimIndent()
                )
            ).rows
        }
 
    /** Alertas de FC fuera de rango (últimas 24 horas) */
    suspend fun obtenerAlertas24h(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT id,bpm,estado,dispositivo,hora,created_at FROM lecturas_fc
                               WHERE (bpm < 60 OR bpm > 100)
                                 AND created_at > NOW() - INTERVAL '24 hours'
                               ORDER BY created_at DESC""".trimIndent()
                )
            ).rows
        }

    /** Promedio de FC por hora del día */
    suspend fun obtenerPromedioPorHora(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT 0 AS id,
                               ROUND(AVG(bpm)) AS bpm,
                               'Lecturas: ' || COUNT(*) AS estado,
                               'Hora ' || EXTRACT(HOUR FROM created_at) AS dispositivo,
                               CAST(EXTRACT(HOUR FROM created_at) AS VARCHAR) || ':00' AS hora,
                               '' AS created_at
                               FROM lecturas_fc
                               GROUP BY EXTRACT(HOUR FROM created_at)
                               ORDER BY EXTRACT(HOUR FROM created_at)""".trimIndent()
                )
            ).rows
        }

    /** Lectura más reciente de cada dispositivo */
    suspend fun obtenerMasRecientePorDispositivo(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT DISTINCT ON (dispositivo)
                               id, bpm, estado, dispositivo, hora, created_at
                               FROM lecturas_fc
                               ORDER BY dispositivo, created_at DESC""".trimIndent()
                )
            ).rows
        }

    /** Detección de taquicardia sostenida (>100 bpm por 3+ lecturas seguidas) */
    suspend fun obtenerTaquicardiaSostenida(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT 0 AS id,
                               CAST(COUNT(*) AS INTEGER) AS bpm,
                               'Taquicardias' AS estado,
                               'Alerta Sostenida' AS dispositivo,
                               MIN(hora) || ' - ' || MAX(hora) AS hora,
                               '' AS created_at
                               FROM lecturas_fc
                               WHERE bpm > 100
                                 AND created_at > NOW() - INTERVAL '1 hour'
                               HAVING COUNT(*) >= 3""".trimIndent()
                )
            ).rows
        }
}
