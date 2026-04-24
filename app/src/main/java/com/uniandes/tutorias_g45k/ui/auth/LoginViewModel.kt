package com.uniandes.tutorias_g45k.ui.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel () : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    private val authRepository = AuthHolder.authRepo
    val state = _state.asStateFlow()

    fun onLoginStarted() {
        _state.update { it.copy(isLoading = true, error = null) }
    }

    fun onLoginSuccess(loginConfirmation:()->Unit) {
        viewModelScope.launch (Dispatchers.IO){
            authRepository.saveLocalUser()
            val user = authRepository.getCurrentUser()
            withContext(Dispatchers.Main){
                loginConfirmation()
                _state.update { it.copy(isLoading = false, user = user) }
            }
        }
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
