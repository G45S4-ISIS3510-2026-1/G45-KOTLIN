package com.example.g45_kotlin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.reservation.ReservationRepository
import com.example.g45_kotlin.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    private val authRepository = AuthHolder.authRepo

    private val userRepository = UserRepository
    private val reservationRepository = ReservationRepository

    val state = _state.asStateFlow()

    val currentTime = flow {
        while (true) {
            emit(LocalDateTime.now(ZoneId.of("America/Bogota"))) // Emite el tiempo actual
            delay(1000)
        }
    }.stateIn(
        viewModelScope,
        started= SharingStarted.WhileSubscribed(2000),
        initialValue = LocalDateTime.now(ZoneId.of("America/Bogota"))
    )

    init {
        loadHomeData()
    }



    fun loadHomeData() {
        _state.update{it.copy(userName = authRepository.getCurrentUser()?.displayName?:"Amigo", error = null, sessionError = null)}
        viewModelScope.launch (Dispatchers.IO){
            val userName=authRepository.getLocalUser() ?: authRepository.getCurrentUser()
            _state.update { it.copy(isLoading = true, areSessionLoading = true) }
            try {
                val sessionResult=reservationRepository.getUpcomingUserSessions(authRepository.getCurrentUser()?.uid ?: "")
                if (sessionResult.isSuccess){
                    _state.update { it.copy(nextSessions = sessionResult.getOrThrow(), areSessionLoading = false) }
                }else{
                    _state.update { it.copy(sessionError = "Error cargando sesiones proximas. Por favor revise su conexión") }
                }
                val response = userRepository.getRecommendations()
                if (response.isSuccessful) {
                    _state.update { it.copy(
                        featuredTutors = response.body()?.take(3) ?: emptyList(),
                        userName = userName?.displayName ?: "Amigo"
                    ) }
                } else {
                    _state.update { it.copy(error = "Error al cargar tutores destacados") }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, areSessionLoading = false, error = e.message) }
            }
            _state.update { it.copy(isLoading = false, areSessionLoading = false) }
        }
    }
}
