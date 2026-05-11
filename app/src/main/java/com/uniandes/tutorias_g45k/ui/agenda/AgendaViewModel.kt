package com.uniandes.tutorias_g45k.ui.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import com.uniandes.tutorias_g45k.utilities.getDaysOfCertainWeek
import com.uniandes.tutorias_g45k.utilities.getDaysOfCurrentCalendarWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AgendaViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AgendaViewState())
    val uiState: StateFlow<AgendaViewState> = _uiState.asStateFlow()

    private val repo= ReservationRepository
    private val userId=AuthHolder.authRepo.getCurrentUserId()

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDay = date) }
        if (!_uiState.value.selectedWeek.contains(date)) {
            _uiState.update { it.copy(selectedWeek = getDaysOfCertainWeek(date)) }
        }
        fetchSessions()
    }

    fun getNextWeek(){
        _uiState.update { it.copy(selectedWeek = getDaysOfCertainWeek(it.selectedWeek.last().plusDays(2))) }
        _uiState.update { it.copy(selectedDay = it.selectedWeek.first()) }
        fetchSessions()
    }

    fun getPreviousWeek(){
        _uiState.update { it.copy(selectedWeek = getDaysOfCertainWeek(it.selectedWeek.first().minusDays(3))) }
        _uiState.update { it.copy(selectedDay = it.selectedWeek.last()) }
        fetchSessions()
    }

    fun fetchStart(){
        _uiState.update { it.copy(selectedWeek = getDaysOfCurrentCalendarWeek())}
        val today = LocalDate.now()
        val selectedDay = if (_uiState.value.selectedWeek.contains(today)) today else _uiState.value.selectedWeek.first()
        _uiState.update { it.copy(selectedDay = selectedDay) }
        fetchSessions()
    }

    fun fetchSessions(){
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val result=repo.getUserSessionsByDate(userId ?:"",_uiState.value.selectedDay)
            if (result.isSuccess){
                withContext(Dispatchers.Main){
                    _uiState.update { it.copy(sessions = result.getOrDefault(emptyList()), isLoading = false) }
                }
            }else{
                withContext(Dispatchers.Main){
                    _uiState.update { it.copy( sessions = emptyList(), isLoading = false) }
                }
            }
        }

    }

    init{
        fetchStart()
    }
}