package com.uniandes.tutorias_g45k.ui.profile.pages.reservations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationListViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ReservationListUiState())
    private val sessionRepository = ReservationRepository
    private val authRepository = AuthHolder.authRepo

    val uiState = _uiState.asStateFlow()

    private val _dayFilter = MutableStateFlow<Int?>(0)
    val dayFilter: StateFlow<Int?> = _dayFilter.asStateFlow()

    private val _isTutor = MutableStateFlow(false)
    val isTutor: StateFlow<Boolean> = _isTutor.asStateFlow()



    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenReservations() {
        Log.d("DEBUG_NOTI", "1. Entrando a listenReservations")
        viewModelScope.launch {combine(_isTutor, _dayFilter){
                    tutor, dayRange-> Pair(tutor, dayRange)
                }
                .flatMapLatest { (tutor,dayRange) ->
                    Log.d("DEBUG_NOTI", "2. Filtro cambiado a: $dayRange")
                    _uiState.update { it.copy(isLoading = true, error = "") }
                    sessionRepository.getUserSessions(authRepository.getCurrentUser()?.uid ?: "", tutor, dayRange)
                }
                .catch { it ->
                    Log.e("ReviewListViewModel", "Error en stream", it)
                    _uiState.update { state ->
                        state.copy(
                            error = "Error recuperando sesiones, por favor revise su conexión a internet y refresque el listado",
                            isLoading = false,
                            sessions = emptyList()
                        )
                    }
                }
                .collect { list ->
                    Log.d("DEBUG_NOTI", "4. DATOS RECIBIDOS: ${list.size} elementos: $list")
                    _uiState.update { it.copy(sessions = list, isLoading = false) }
                }
        }
    }

    fun onSelectRange(range: Int?) {
        _dayFilter.value = range
    }

    fun onSelectTutor(isTutor:Boolean){
        _isTutor.value=isTutor
        reLoadSessions()
    }

    fun reLoadSessions() {
        _uiState.update { it.copy(error = "") }
        val current = _dayFilter.value
        _dayFilter.value = current
    }

    init{
        listenReservations()
    }

}