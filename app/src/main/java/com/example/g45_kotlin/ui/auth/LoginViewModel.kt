package com.uniandes.tutorias_g45k.ui.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel () : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    private val authRepository = AuthHolder.authRepo
    val isOnline = NetworkMonitor.isOnline

    val state = combine(_state, isOnline) { state, online ->
        state.copy(error = if (!online) "No hay conexión a internet. Los datos mostrados podrían estar desactualizados." else state.error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoginState())

    fun onLoginStarted() {
        if (!isOnline.value) {
            _state.update { it.copy(error = "No puedes iniciar sesión sin conexión.") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
    }

    fun onLoginSuccess(loginConfirmation:()->Unit) {
        viewModelScope.launch (Dispatchers.IO){
            authRepository.saveLocalUser()
        }
        val user = authRepository.getCurrentUser()
        loginConfirmation()
        _state.update { it.copy(isLoading = false, user = user) }
    }

    fun onNewLogin(){
        viewModelScope.launch (Dispatchers.IO){
            authRepository.saveBackendUser()
        }
    }

    fun onPreviousLogin(){
        viewModelScope.launch (Dispatchers.IO){
            authRepository.saveFcmToken()
        }
    }

    fun onLoginError(message: String) {
        _state.update { it.copy(isLoading = false, error = message) }
    }
}

