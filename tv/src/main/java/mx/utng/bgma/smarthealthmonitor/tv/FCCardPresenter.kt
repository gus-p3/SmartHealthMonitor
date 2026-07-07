// tv/src/main/java/mx/utng/bgma/smarthealthmonitor/tv/FCCardPresenter.kt
package mx.utng.bgma.smarthealthmonitor.tv

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC

/**
 * FCCardPresenter — Presenter de Leanback para mostrar lecturas de FC.
 *
 * Arquitectura clave:
 *   BrowseSupportFragment → ListRow → ArrayObjectAdapter → **FCCardPresenter** → ImageCardView
 *
 * Este patrón es exactamente el que usa YouTube TV, Netflix y Cinépolis en Android TV.
 * El Presenter convierte un objeto de datos (LecturaFC) en una View (ImageCardView).
 */
class FCCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            // CRÍTICO: sin estas dos líneas el D-pad no puede navegar a este card
            isFocusable            = true
            isFocusableInTouchMode = true
            // Dimensiones de la imagen principal de la card
            setMainImageDimensions(240, 180)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val card    = viewHolder.view as ImageCardView
        val lectura = item as LecturaFC

        card.titleText   = "${lectura.valorBpm} bpm"
        card.contentText = lectura.hora

        // Color de fondo según si la FC es normal (60–100 bpm)
        val bgColor = if (lectura.esNormal) {
            Color.parseColor("#1B4F8A")   // sh_primary — FC normal
        } else {
            Color.parseColor("#B3261E")   // sh_error   — FC fuera de rango
        }
        card.setBackgroundColor(bgColor)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Liberar imagen al reciclar la card para evitar memory leaks
        (viewHolder.view as ImageCardView).mainImage = null
    }
}
