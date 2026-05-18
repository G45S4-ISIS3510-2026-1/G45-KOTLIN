package com.uniandes.tutorias_g45k.ui.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto
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
import java.time.OffsetDateTime

class AgendaViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AgendaViewState())
    val uiState: StateFlow<AgendaViewState> = _uiState.asStateFlow()

    private val repo= ReservationRepository
    private val authRepo= AuthHolder.authRepo

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

    fun selectWeekDay(day:Int){
        _uiState.update { it.copy(selectedWeekDay = day) }
        val days=when(day){
            0 -> _uiState.value.availability.monday
            1 -> _uiState.value.availability.tuesday
            2 -> _uiState.value.availability.wednesday
            3 -> _uiState.value.availability.thursday
            4 -> _uiState.value.availability.friday
            5 -> _uiState.value.availability.saturday
            else -> emptyList()
        }
        _uiState.update { it.copy(hours = getHours(days))
        }
    }

    fun getHours(availability: List<String>):List<Int>{
        return availability.map{OffsetDateTime.parse(it).hour}
    }

    fun fetchStart(){
        _uiState.update { it.copy(selectedWeek = getDaysOfCurrentCalendarWeek(), isLoadingAvailability = true, error = null)}
        val today = LocalDate.now()
        val selectedDay = if (_uiState.value.selectedWeek.contains(today)) today else _uiState.value.selectedWeek.first()
        _uiState.update { it.copy(selectedDay = selectedDay) }
        fetchAvailability()
        fetchSessions()
    }

    fun fetchAvailability(){
        _uiState.update { it.copy(isLoadingAvailability = true, errorAvailability = null)}
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val user=authRepo.getLocalUser()
                withContext(Dispatchers.Main){
                    _uiState.update { it.copy(availability = user?.availability ?: AvailabilityDto(), isTutor = user?.isTutoring ?: false, selectedWeekDay = 0) }
                    _uiState.update { it.copy(hours = getHours(it.availability.monday), isLoadingAvailability = false) }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    _uiState.update { it.copy(errorAvailability = e.message ?: "Error al obtener la disponibilidad", isLoadingAvailability = false) }
                }
            }
        }
    }

    fun fetchSessions(){
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val result=repo.getUserSessionsByDate(authRepo.getCurrentUserId() ?:"",_uiState.value.selectedDay)
                if (result.isSuccess){
                    withContext(Dispatchers.Main){
                        _uiState.update { it.copy(sessions = result.getOrDefault(emptyList()), isLoading = false) }
                    }
                }else{
                    withContext(Dispatchers.Main){
                        _uiState.update { it.copy( sessions = emptyList(), isLoading = false) }
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    _uiState.update { it.copy(error = e.message ?: "Error al obtener las sesiones", isLoading = false) }
                }
            }
        }

    }

    init{
        fetchStart()
    }
}