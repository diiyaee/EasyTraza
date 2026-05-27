package cat.copernic.easytrazaapp.feature.lot.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytrazaapp.core.DTOs.LotDTO
import cat.copernic.easytrazaapp.core.error.ErrorMapper
import cat.copernic.easytrazaapp.core.network.ApiClient
import cat.copernic.easytrazaapp.feature.lot.data.repository.LotRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LotRepository(
        ApiClient.getLotApi(application)
    )

    var lots by mutableStateOf<List<LotDTO>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        carregarLots()
    }

    fun carregarLots() {
        viewModelScope.launch {
            try {
                error = null
                lots = repository.getLots()
            } catch (e: Exception) {
                error = ErrorMapper.getFriendlyMessage(e)
            }
        }
    }

    fun canviarEstat(
        id: Long,
        forcar: Boolean = false,
        onConflict: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                error = null
                repository.canviarEstat(id, forcar)
                carregarLots()
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    onConflict()
                } else {
                    error = ErrorMapper.getFriendlyMessage(e)
                }
            } catch (e: Exception) {
                error = ErrorMapper.getFriendlyMessage(e)
            }
        }
    }
}