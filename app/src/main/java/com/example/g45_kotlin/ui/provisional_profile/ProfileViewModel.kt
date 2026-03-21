package com.example.g45_kotlin.ui.provisional_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.reservation.ReservationRepository
import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.ui.reservation.gateway.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    private val sessionRepository = ReservationRepository
    val uiState = _uiState.asStateFlow()

    private var sessionList: MutableSet<SessionDto> = mutableSetOf()

    fun retriveSessions(){
        sessionList.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val response = sessionRepository.getAll()
            if (response.isSuccessful) {
                val sessions = response.body()
                sessions?.forEach { session ->
                    sessionList.add(session)
                }
                _uiState.value = _uiState.value.copy(sessions = sessionList)
                _uiState.value = _uiState.value.copy(numSessions = sessionList.size)
            }
        }
    }



    init{
        retriveSessions()
    }

}