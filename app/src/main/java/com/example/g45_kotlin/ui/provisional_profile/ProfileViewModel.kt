package com.uniandes.tutorias_g45k.ui.provisional_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import com.uniandes.tutorias_g45k.data.reservation.SessionDto
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    private val authRepository = AuthHolder.authRepo
    private val sessionRepository = ReservationRepository(authRepository.getSessionDao())

    val uiState = _uiState.asStateFlow()

    private var sessionList: MutableSet<SessionDto> = mutableSetOf()

    private fun observeNetwork() {
        Log.d("NetworkMonitor", "Observing network changes...")
        NetworkMonitor.isOnline.onEach { isConnected ->
            if (isConnected) {
                Log.d("NetworkMonitor", "Network is connected")
                _uiState.value = _uiState.value.copy(error = "", connected = true)
            } else {
                Log.d("NetworkMonitor", "Network is not connected")
                _uiState.value = _uiState.value.copy(error = "No hay conexion a internet. Revise su conexion e intente nuevamente", connected = false)
            }
        }.launchIn(viewModelScope)
    }

    fun retriveSessions(){
        sessionList.clear()
        _uiState.value = _uiState.value.copy(sessions = sessionList, error = "", isLoading = true)
        if (!NetworkMonitor.isOnline.value){
            _uiState.value = _uiState.value.copy(error = "No hay conexion a internet. Revise su conexion e intente nuevamente", isLoading = false)
            return
        }
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
        observeNetwork()
        retriveSessions()
    }

}
