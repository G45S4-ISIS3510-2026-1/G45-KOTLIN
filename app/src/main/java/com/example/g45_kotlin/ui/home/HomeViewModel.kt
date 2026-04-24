package com.example.g45_kotlin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.user.UserRepository
import com.example.g45_kotlin.utilities.AnalyticsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    private val authRepository = AuthHolder.authRepo
    val state = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        _state.update{it.copy(userName = authRepository.getCurrentUser()?.displayName?:"Amigo")}
        viewModelScope.launch (Dispatchers.IO){
            val userName=authRepository.getLocalUser() ?: authRepository.getCurrentUser()
            _state.update { it.copy(isLoading = true) }
            try {
                val response = UserRepository.getTutors()
                if (response.isSuccessful) {
                    _state.update { it.copy(
                        featuredTutors = response.body()?.take(3) ?: emptyList(),
                        isLoading = false,
                        userName = userName?.displayName ?: "Amigo"
                    ) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Error al cargar tutores destacados") }
                }
            } catch (e: Exception) {
                AnalyticsManager.logError("HOME_SERVICE", "Failed to load tutors", e)
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
