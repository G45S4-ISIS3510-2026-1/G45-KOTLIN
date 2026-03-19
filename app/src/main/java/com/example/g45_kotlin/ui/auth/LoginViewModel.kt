package com.example.g45_kotlin.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.g45_kotlin.data.auth.AuthRepository

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onLoginStarted() {
        _state.update { it.copy(isLoading = true, error = null) }
    }

    fun onLoginSuccess() {
        val user = AuthRepository.getCurrentUser()
        _state.update { it.copy(isLoading = false, user = user) }
    }

    fun onLoginError(message: String) {
        _state.update { it.copy(isLoading = false, error = message) }
    }
}
