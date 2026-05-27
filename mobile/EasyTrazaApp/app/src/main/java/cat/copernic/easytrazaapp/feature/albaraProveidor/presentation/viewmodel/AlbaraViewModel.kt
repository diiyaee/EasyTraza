package cat.copernic.easytrazaapp.feature.albaraProveidor.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytrazaapp.core.DTOs.AlbaraFormDTO
import cat.copernic.easytrazaapp.core.DTOs.AlbaraResponseDTO
import cat.copernic.easytrazaapp.core.error.ErrorMapper
import cat.copernic.easytrazaapp.core.network.ApiClient
import cat.copernic.easytrazaapp.feature.albaraProveidor.data.repositories.AlbaraRepository
import kotlinx.coroutines.launch

class AlbaraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AlbaraRepository(
        ApiClient.getAlbaraApi(application)
    )

    var albaranes by mutableStateOf<List<AlbaraResponseDTO>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadAlbaranes() {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                albaranes = repository.getAll()
            } catch (e: Exception) {
                error = ErrorMapper.getFriendlyMessage(e)
            } finally {
                loading = false
            }
        }
    }
}