package com.uniandes.tutorias_g45k.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.recommendation.RecommendedUserRepository
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    private val authRepository = AuthHolder.authRepo

    private val recommendationRepository = RecommendedUserRepository
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
        _state.update{it.copy(userName = authRepository.getCurrentUser()?.displayName?:"Amigo", error = null, sessionError = null, isLoading = true, areSessionLoading = true)}
        viewModelScope.launch (Dispatchers.IO){
            val user=authRepository.getLocalUser() ?: authRepository.getCurrentUser()
            if (user!=null){
                _state.update { it.copy(isTutor = user.isTutoring) }
            }
            try {
                val sessionResult=reservationRepository.getUpcomingUserSessions(authRepository.getCurrentUser()?.uid ?: "")
                withContext(Dispatchers.Main){
                    if (sessionResult.isSuccess){
                        _state.update { it.copy(nextSessions = sessionResult.getOrThrow(), areSessionLoading = false) }
                    }else{
                        _state.update { it.copy(sessionError = "Error cargando sesiones proximas. Por favor revise su conexión") }
                    }
                }
                val response = recommendationRepository.getRecommendations()
                withContext(Dispatchers.Main) {
                    if (response.isSuccess) {
                        _state.update { it.copy(
                            featuredTutors = response.getOrNull() ?: emptyList()
                        ) }
                    } else {
                        _state.update { it.copy(error = "Error al cargar tutores recomendados. Revise su conexión y arrastre para reintentarlo") }
                    }
                    _state.update { it.copy(isLoading = false, areSessionLoading = false) }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(isLoading = false, areSessionLoading = false, error = e.message) }
                }
            }
        }
    }
}
