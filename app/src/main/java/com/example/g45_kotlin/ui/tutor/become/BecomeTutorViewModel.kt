package com.example.g45_kotlin.ui.tutor.become

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.catalog.TutorRepository
import com.example.g45_kotlin.data.local.BecomeTutorDraftManager
import com.example.g45_kotlin.data.reservation.AvailabilityDto
import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.example.g45_kotlin.data.user.UserRepository
import com.example.g45_kotlin.utilities.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class BecomeTutorUiState(
    val majors: List<String> = emptyList(),
    val skillsByMajor: Map<String, List<SkillSummaryDto>> = emptyMap(),
    val expandedMajors: Set<String> = emptySet(),
    val selectedSkills: Set<String> = emptySet(),
    val selectedMajors: Set<String> = emptySet(),
    val sessionPrice: Int = 0,
    val availability: Map<String, List<TimeSlot>> = emptyMap(),
    val selectedDay: String = "LUN",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

data class TimeSlot(
    val id: String = java.util.UUID.randomUUID().toString(),
    val from: String = "08:00",
    val to: String = "09:00"
)

class BecomeTutorViewModel(
    private val repository: TutorRepository = TutorRepository,
    private val userRepository: UserRepository = UserRepository,
    private val draftManager: BecomeTutorDraftManager? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(BecomeTutorUiState())
    val uiState: StateFlow<BecomeTutorUiState> = _uiState.asStateFlow()

    init {
        observeNetwork()
        loadMajors()
        loadDraft()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            NetworkMonitor.isOnline.collect { isOnline ->
                if (isOnline && _uiState.value.majors.isEmpty() && _uiState.value.error != null) {
                    loadMajors()
                }
            }
        }
    }

    private fun loadDraft() {
        draftManager?.getDraft()?.let { draft ->
            _uiState.update { it.copy(
                selectedSkills = draft.selectedSkills,
                selectedMajors = draft.selectedMajors,
                sessionPrice = draft.sessionPrice,
                availability = draft.availability
            ) }
        }
    }

    private fun saveDraft() {
        val state = _uiState.value
        draftManager?.saveDraft(
            selectedSkills = state.selectedSkills,
            selectedMajors = state.selectedMajors,
            sessionPrice = state.sessionPrice,
            availability = state.availability
        )
    }

    private fun loadMajors() {
        if (!NetworkMonitor.isOnline.value) {
            _uiState.update { it.copy(error = "Sin conexión para cargar facultades") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getMajors().onSuccess { majors ->
                _uiState.update { it.copy(majors = majors, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Error cargando habilidades/carreras. Revise su conexion, y vuelva a intentarlo mas tarde", isLoading = false, majors = emptyList()) }
            }
        }
    }

    fun toggleMajor(major: String) {
        val currentExpanded = _uiState.value.expandedMajors
        if (currentExpanded.contains(major)) {
            _uiState.update { it.copy(expandedMajors = currentExpanded - major) }
        } else {
            _uiState.update { it.copy(expandedMajors = currentExpanded + major) }
            if (!_uiState.value.skillsByMajor.containsKey(major)) {
                loadSkillsForMajor(major)
            }
        }
    }

    fun toggleMajorSelection(major: String) {
        _uiState.update { state ->
            val currentSelected = state.selectedMajors
            val newSelected = if (currentSelected.contains(major)) {
                currentSelected - major
            } else {
                currentSelected + major
            }
            state.copy(selectedMajors = newSelected)
        }
        saveDraft()
    }

    private fun loadSkillsForMajor(major: String) {
        if (!NetworkMonitor.isOnline.value) return
        viewModelScope.launch {
            repository.getSkillsByMajor(major).onSuccess { skills ->
                _uiState.update { state ->
                    state.copy(skillsByMajor = state.skillsByMajor + (major to skills))
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(skillsByMajor = state.skillsByMajor + (major to emptyList()))
                }
            }
        }
    }

    fun toggleSkill(skillId: String) {
        val currentSelected = _uiState.value.selectedSkills
        if (currentSelected.contains(skillId)) {
            _uiState.update { it.copy(selectedSkills = currentSelected - skillId) }
        } else {
            _uiState.update { it.copy(selectedSkills = currentSelected + skillId) }
        }
        saveDraft()
    }

    fun updatePrice(price: Int) {
        _uiState.update { it.copy(sessionPrice = price) }
        saveDraft()
    }

    fun selectDay(day: String) {
        _uiState.update { it.copy(selectedDay = day) }
    }

    fun addTimeSlot(day: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val newSlots = currentSlots + TimeSlot()
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
        saveDraft()
    }

    fun removeTimeSlot(day: String, slotId: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val newSlots = currentSlots.filter { it.id != slotId }
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
        saveDraft()
    }

    fun updateStartTime(day: String, slotId: String, from: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val to = calculateEndTime(from)
        val newSlots = currentSlots.map {
            if (it.id == slotId) it.copy(from = from, to = to) else it
        }
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
        saveDraft()
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

    fun publishProfile() {
        if (!NetworkMonitor.isOnline.value) {
            _uiState.update { it.copy(error = "Sin conexión para publicar perfil") }
            return
        }

        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return
        val currentState = _uiState.value
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // 1. Mapear disponibilidad
                val apiAvailability = AvailabilityDto(
                    monday = currentState.availability["LUN"]?.map { formatToIso(it.from) } ?: emptyList(),
                    tuesday = currentState.availability["MAR"]?.map { formatToIso(it.from) } ?: emptyList(),
                    wednesday = currentState.availability["MIE"]?.map { formatToIso(it.from) } ?: emptyList(),
                    thursday = currentState.availability["JUE"]?.map { formatToIso(it.from) } ?: emptyList(),
                    friday = currentState.availability["VIE"]?.map { formatToIso(it.from) } ?: emptyList(),
                    saturday = currentState.availability["SAB"]?.map { formatToIso(it.from) } ?: emptyList()
                )

                // 2. Realizar las peticiones PATCH de forma secuencial
                
                // Actualizar Carrera (Major)
                val selectedMajor = currentState.selectedMajors.firstOrNull()
                if (selectedMajor != null) {
                    val majorResponse = userRepository.updateMajor(userId, selectedMajor)
                    if (!majorResponse.isSuccessful) {
                        _uiState.update { it.copy(isLoading = false, error = "Error actualizando carrera: ${majorResponse.errorBody()?.string()}") }
                        return@launch
                    }
                }

                // Actualizar Habilidades
                val skillsResponse = userRepository.updateTutoringSkills(userId, currentState.selectedSkills.toList())
                if (!skillsResponse.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, error = "Error actualizando habilidades: ${skillsResponse.errorBody()?.string()}") }
                    return@launch
                }

                // Actualizar Disponibilidad
                val availabilityResponse = userRepository.updateAvailability(userId, apiAvailability)
                if (!availabilityResponse.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, error = "Error actualizando disponibilidad: ${availabilityResponse.errorBody()?.string()}") }
                    return@launch
                }

                // Actualizar Precio
                val priceResponse = userRepository.updateSessionPrice(userId, currentState.sessionPrice)
                if (!priceResponse.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, error = "Error actualizando precio: ${priceResponse.errorBody()?.string()}") }
                    return@launch
                }

                // Si todas fueron exitosas
                draftManager?.clearDraft()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error inesperado: ${e.message}") }
            }
        }
    }
    
    private fun formatToIso(time: String): String {
        return "2024-11-20T$time:00Z"
    }
}
