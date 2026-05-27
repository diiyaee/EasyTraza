package cat.copernic.easytrazaapp.feature.login.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytrazaapp.core.DTOs.Usuari
import cat.copernic.easytrazaapp.core.error.ErrorMapper
import cat.copernic.easytrazaapp.core.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val usuaris: List<Usuari>) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Loading)
    val state: StateFlow<LoginState> = _state

    fun fetchUsuaris(context: Context) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val response = ApiClient.getUsuariApi(context).findAll()

                if (response.isSuccessful && response.body() != null) {
                    _state.value = LoginState.Success(response.body()!!)
                } else {
                    val friendlyMessage = ErrorMapper.getFriendlyMessage(null, response.code())
                    _state.value = LoginState.Error(friendlyMessage)
                }
            } catch (e: Exception) {
                val friendlyMessage = ErrorMapper.getFriendlyMessage(e, null)
                _state.value = LoginState.Error(friendlyMessage)
            }
        }
    }
}