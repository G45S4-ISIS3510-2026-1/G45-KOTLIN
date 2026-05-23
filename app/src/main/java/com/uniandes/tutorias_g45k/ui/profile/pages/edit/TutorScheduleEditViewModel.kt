package com.uniandes.tutorias_g45k.ui.profile.pages.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto
import com.uniandes.tutorias_g45k.data.user.UserRepository
import com.uniandes.tutorias_g45k.ui.tutor.become.TimeSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.collections.map



class TutorScheduleEditViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(TutorScheduleEditState())
    val uiState: StateFlow<TutorScheduleEditState> = _uiState.asStateFlow()

    private val authRepo = AuthHolder.authRepo
    private val userRepository: UserRepository = UserRepository

    private fun formatToIso(time: String): String {
        return "2024-11-20T$time:00Z"
    }

    private fun formatFromIso(time: String): TimeSlot {
        val parts = time.split("T")
        val timeParts = parts[1].split(":")
        val hour = timeParts[0].toInt()
        return TimeSlot(from="${if (hour < 10) "0" else ""}$hour:00", to="${if ((hour + 1) % 24 < 10) "0" else ""}${(hour + 1) % 24}:00")
    }

    private fun formatAvailability(): AvailabilityDto{
        val currentAvailability = _uiState.value.availability
        val apiAvailability = AvailabilityDto(
            monday = currentAvailability["LUN"]?.map { formatToIso(it.from) } ?: emptyList(),
            tuesday = currentAvailability["MAR"]?.map { formatToIso(it.from) } ?: emptyList(),
            wednesday = currentAvailability["MIE"]?.map { formatToIso(it.from) } ?: emptyList(),
            thursday = currentAvailability["JUE"]?.map { formatToIso(it.from) } ?: emptyList(),
            friday = currentAvailability["VIE"]?.map { formatToIso(it.from) } ?: emptyList(),
            saturday = currentAvailability["SAB"]?.map { formatToIso(it.from) } ?: emptyList()
        )
        return apiAvailability
    }

    private fun formatSchedule(availability:AvailabilityDto) {

    }

    fun selectDay(day: String) {
        _uiState.update { it.copy(selectedDay = day) }
    }

    fun addTimeSlot(day: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val newSlots = currentSlots + TimeSlot()
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
    }

    fun removeTimeSlot(day: String, slotId: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val newSlots = currentSlots.filter { it.id != slotId }
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
    }

    private fun fetchAvailability() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val currentAvailability=authRepo.getLocalUser()?.availability
                val avMap= mutableMapOf<String, List<TimeSlot>>()
                if (currentAvailability!=null) {
                    avMap["LUN"] = currentAvailability.monday.map { formatFromIso(it) }
                    avMap["MAR"] = currentAvailability.tuesday.map { formatFromIso(it) }
                    avMap["MIE"] = currentAvailability.wednesday.map { formatFromIso(it) }
                    avMap["JUE"] = currentAvailability.thursday.map { formatFromIso(it) }
                    avMap["VIE"] = currentAvailability.friday.map { formatFromIso(it) }
                    avMap["SAB"] = currentAvailability.saturday.map { formatFromIso(it) }
                }
                withContext(Dispatchers.Main) { _uiState.update { it.copy(availability = avMap, isLoading = false)} }
            }catch (e: Exception){
                withContext(Dispatchers.Main) { _uiState.update { it.copy(error = "Error recuperando disponibilidad actual", isLoading = false) } }
            }
        }
    }
    fun updateStartTime(day: String, slotId: String, from: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val to = calculateEndTime(from)
        val newSlots = currentSlots.map {
            if (it.id == slotId) it.copy(from = from, to = to) else it
        }
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
    }

    private fun calculateEndTime(from: String): String {
        return try {
            val parts = from.split(":")
            val hour = parts[0].toInt()
            val minutes = if (parts.size > 1) parts[1] else "00"
            val nextHour = (hour + 1) % 24
            String.format(Locale.getDefault(), "%02d:%s", nextHour, minutes)
        } catch (e: Exception) {
            ""
        }
    }

    fun saveAvailability(onSucces: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val apiAvailability=formatAvailability()
                val result = userRepository.updateAvailability(authRepo.getCurrentUserId() ?: "", apiAvailability)
                if (result.isSuccessful) {
                    authRepo.updateLocalUser(authRepo.getLocalUser()?.copy(availability = apiAvailability) ?: return@launch)
                    withContext(Dispatchers.Main) { _uiState.update { it.copy(isLoading = false) }; onSucces() }

                } else {
                    withContext(Dispatchers.Main) { _uiState.update { it.copy(error = "Error guardando disponibilidad", isLoading = false) } }
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main) { _uiState.update { it.copy(error = "Error guardando disponibilidad", isLoading = false) } }
            }

        }
    }

    init{
        fetchAvailability()
    }
}
