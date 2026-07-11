package mx.utng.smarthealthmonitor.cast
import android.content.Context
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.CastMediaOptions
 
class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(ctx: Context): CastOptions =
        CastOptions.Builder()
            .setReceiverApplicationId(
                CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID
            )
            .build()
 
    override fun getAdditionalSessionProviders(ctx: Context) = emptyList<SessionProvider>()
}
