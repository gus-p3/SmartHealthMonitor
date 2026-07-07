// tv/src/main/java/mx/utng/bgma/smarthealthmonitor/tv/MainFragment.kt
package mx.utng.bgma.smarthealthmonitor.tv

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC

/**
 * MainFragment — corazón de la UI para Android TV.
 *
 * Extiende BrowseSupportFragment (Leanback), que proporciona:
 *   • Sidebar izquierdo con categorías (headers)
 *   • Filas horizontales de cards navegables con D-pad
 *   • Animaciones de foco y transición entre categorías
 *
 * Arquitectura:
 *   MainActivity → MainFragment (BrowseSupportFragment)
 *                       ↓
 *               ArrayObjectAdapter (filas)
 *                       ↓
 *               ListRow (fila individual)
 *                       ↓
 *               FCCardPresenter → ImageCardView
 */
class MainFragment : BrowseSupportFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Configuración general del BrowseFragment ──────────────────
        title        = "SmartHealth TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Color de la marca en el sidebar izquierdo — sh_primary
        brandColor = Color.parseColor("#1B4F8A")

        cargarFilas()
    }

    // ─────────────────────────────────────────────────────────────────
    //  Datos simulados — en Ej.03 vendrán de Room vía TvViewModel
    // ─────────────────────────────────────────────────────────────────

    /** Historial de FC simulado (7 lecturas de distintas horas). */
    private val historialMock = listOf(
        LecturaFC(id = 1, valorBpm = 78,  hora = "11:00"),
        LecturaFC(id = 2, valorBpm = 82,  hora = "10:30"),
        LecturaFC(id = 3, valorBpm = 76,  hora = "10:00"),
        LecturaFC(id = 4, valorBpm = 115, hora = "09:30"),   // fuera de rango → rojo
        LecturaFC(id = 5, valorBpm = 71,  hora = "09:00"),
        LecturaFC(id = 6, valorBpm = 80,  hora = "08:30"),
        LecturaFC(id = 7, valorBpm = 74,  hora = "08:00")
    )

    /**
     * ⭐ Reto adicional — Alertas recientes simuladas.
     * FC > 100 bpm (taquicardia) o < 60 bpm (bradicardia) → card en rojo.
     */
    private val alertasMock = listOf(
        LecturaFC(id = 10, valorBpm = 130, hora = "07:15"),  // taquicardia
        LecturaFC(id = 11, valorBpm = 48,  hora = "06:50"),  // bradicardia
        LecturaFC(id = 12, valorBpm = 122, hora = "06:10")   // taquicardia
    )

    // ─────────────────────────────────────────────────────────────────
    //  Construcción de filas
    // ─────────────────────────────────────────────────────────────────

    private fun cargarFilas() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        // ── Fila 1: Estado actual (FC + Pasos) ────────────────────────
        val estadoAdapter = ArrayObjectAdapter(FCCardPresenter())
        estadoAdapter.add(LecturaFC(id = 0,  valorBpm = 88,   hora = "Ahora"))
        estadoAdapter.add(LecturaFC(id = -1, valorBpm = 4250, hora = "Pasos"))  // > 100 → rojo
        rowsAdapter.add(ListRow(HeaderItem("Estado actual"), estadoAdapter))

        // ── Fila 2: Historial de FC ───────────────────────────────────
        val histAdapter = ArrayObjectAdapter(FCCardPresenter())
        historialMock.forEach { histAdapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem("Historial FC"), histAdapter))

        // ── Fila 3: Alertas recientes (⭐ reto adicional) ─────────────
        // Reutiliza FCCardPresenter — las alertas con FC fuera de rango
        // se colorean en rojo automáticamente por la lógica del presenter.
        val alertasAdapter = ArrayObjectAdapter(FCCardPresenter())
        alertasMock.forEach { alertasAdapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem("Alertas recientes"), alertasAdapter))

        adapter = rowsAdapter
    }
}
