package com.uniandes.tutorias_g45k.ui.auth


import android.util.Log
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
import kotlinx.coroutines.withContext

class LoginViewModel () : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    private val authRepository = AuthHolder.authRepo
    val isOnline = NetworkMonitor.isOnline

    val state = combine(_state, isOnline) { state, online ->
        state.copy(error = if (!online) "No hay conexión a internet. Restablece tu conexión para iniciar sesión." else state.error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoginState())

    fun onLoginStarted() {
        if (!isOnline.value) {
            _state.update { it.copy(error = "No puedes iniciar sesión sin conexión.") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
    }

    fun onLoginSuccess(loginConfirmation:()->Unit, onFail:()->Unit) {
        viewModelScope.launch (Dispatchers.IO){
            val result=authRepository.saveLocalUser()
            val user = authRepository.getCurrentUser()
            if(result.isSuccess){
                withContext(Dispatchers.Main){
                    loginConfirmation()
                    _state.update { it.copy(isLoading = false, user = user) }
                }
            }else{
                withContext(Dispatchers.Main){
                    Log.d("LoginViewModel", "Error al cargar usuario local")
                    onFail()
                    withContext(Dispatchers.IO){authRepository.signOut()}
                    _state.update { it.copy(isLoading = false, error = "Error cargando datos de cuenta. Intente mas tarde") }
                }
            }

        }
    }

    fun onNewLogin(onFail:()->Unit){
        viewModelScope.launch (Dispatchers.IO){
            val result_one=authRepository.saveBackendUser()
            val user = authRepository.getCurrentUser()
            withContext(Dispatchers.Main){
                if(result_one.isSuccess){
                    _state.update { it.copy(isLoading = false, user = user) }
                }else{
                    onFail()
                    Log.d("LoginViewModel", "Error al registrar usuario en backend")
                    withContext(Dispatchers.IO){authRepository.deleteAccount()}
                    _state.update { it.copy(isLoading = false, error = "Error registrando datos de cuenta. Intente mas tarde") }
                }
            }
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
