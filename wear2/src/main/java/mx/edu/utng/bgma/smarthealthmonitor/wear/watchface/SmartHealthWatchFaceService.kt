package mx.edu.utng.bgma.smarthealthmonitor.wear.watchface

import android.view.SurfaceHolder
import androidx.wear.watchface.*
import androidx.wear.watchface.style.*

class SmartHealthWatchFaceService : WatchFaceService() {

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = SmartHealthRenderer(
            context                    = applicationContext,
            surfaceHolder              = surfaceHolder,
            watchState                 = watchState,
            complicationSlotsManager   = complicationSlotsManager,
            currentUserStyleRepository = currentUserStyleRepository,
            updateDelayMillis          = 1_000L // Nombre corregido para coincidir con el Renderer
        )
        
        return WatchFace(
            watchFaceType = WatchFaceType.DIGITAL,
            renderer = renderer
        )
    }
}
