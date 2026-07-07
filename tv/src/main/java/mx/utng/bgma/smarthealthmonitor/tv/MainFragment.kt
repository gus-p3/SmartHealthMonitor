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

/**
 * MainFragment extiende BrowseSupportFragment de Leanback.
 * Este Fragment muestra el contenido principal en forma de filas de cards
 * navegables con D-pad — el patrón usado por YouTube TV, Netflix y Cinépolis.
 *
 * Ejercicio 01: stub inicial para verificar el setup del módulo.
 * Ejercicio 02: se agregan FCCardPresenter y las filas de datos.
 */
class MainFragment : BrowseSupportFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Título que aparece en el header del BrowseFragment
        title = "SmartHealth TV"

        // Mostrar los encabezados de categorías en el sidebar izquierdo
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Color de la marca (sidebar) — sh_primary #1B4F8A
        brandColor = Color.parseColor("#1B4F8A")

        // Adapter de filas vacío en Ej.01 — se llena en Ej.02
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        rowsAdapter.add(
            ListRow(HeaderItem("Bienvenido a SmartHealth TV"), ArrayObjectAdapter())
        )
        adapter = rowsAdapter
    }
}
