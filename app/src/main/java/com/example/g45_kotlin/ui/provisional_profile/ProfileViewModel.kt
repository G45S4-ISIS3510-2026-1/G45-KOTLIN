package com.example.g45_kotlin.ui.provisional_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.reservation.ReservationRepository
import com.example.g45_kotlin.data.reservation.SessionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    private val sessionRepository = ReservationRepository
    private val authRepository = AuthHolder.authRepo

    val uiState = _uiState.asStateFlow()

    private var sessionList: MutableSet<SessionDto> = mutableSetOf()

    fun retriveSessions(){
        sessionList.clear()
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try{
                val response = sessionRepository.getUserSessions(authRepository.getCurrentUser()?.uid ?: "")
                if (response.isSuccessful) {
                    val sessions = response.body()
                    sessions?.forEach { session ->
                        sessionList.add(session)
                    }
                    _uiState.value = _uiState.value.copy(sessions = sessionList)
                    _uiState.value = _uiState.value.copy(numSessions = sessionList.size)
                }else{
                    val errorCode=response.code()
                    val message=when (errorCode){
                        404 -> "No se encontraron sesiones"
                        else -> "Error al cargar las sesiones"
                    }
                    _uiState.value = _uiState.value.copy(error = message)
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch(e:Exception){
                _uiState.value = _uiState.value.copy(error = e.message ?: "Error desconocido, revisar mas tarde")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    init{
        retriveSessions()
    }

}