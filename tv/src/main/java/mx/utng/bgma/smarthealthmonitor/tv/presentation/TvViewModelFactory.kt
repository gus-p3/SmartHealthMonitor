package mx.utng.bgma.smarthealthmonitor.tv.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.utng.bgma.smarthealthmonitor.data.SmartHealthRepository

class TvViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvViewModel::class.java)) {
            SmartHealthRepository.init(context.applicationContext)
            return TvViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
