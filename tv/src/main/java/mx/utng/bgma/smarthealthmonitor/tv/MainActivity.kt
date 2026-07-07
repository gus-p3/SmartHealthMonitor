// tv/src/main/java/mx/utng/bgma/smarthealthmonitor/tv/MainActivity.kt
package mx.utng.bgma.smarthealthmonitor.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * MainActivity para Android TV.
 * Es solo el contenedor: carga MainFragment.
 * TODA la lógica de UI va en el Fragment (patrón Leanback).
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Solo agregar el Fragment si es la primera vez (evitar duplicados al rotar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commit()
        }
    }
}
