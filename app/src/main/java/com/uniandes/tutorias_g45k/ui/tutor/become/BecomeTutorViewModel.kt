package com.uniandes.tutorias_g45k.ui.tutor.become

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.catalog.TutorRepository
import com.uniandes.tutorias_g45k.data.local.BecomeTutorDraftManager
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto
import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto
import com.uniandes.tutorias_g45k.data.user.UserRepository
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val isOnline: Boolean = true,
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
                _uiState.update { it.copy(isOnline = isOnline) }
                if (isOnline && _uiState.value.majors.isEmpty() && _uiState.value.error != null) {
                    loadMajors()
                }
            }
        }
    }

    private fun loadDraft() {
        viewModelScope.launch(Dispatchers.IO) {
            draftManager?.getDraft()?.let { draft ->
                _uiState.update { it.copy(
                    selectedSkills = draft.selectedSkills,
                    selectedMajors = draft.selectedMajors,
                    sessionPrice = draft.sessionPrice,
                    availability = draft.availability
                ) }
            }
        }
    }

    private fun saveDraft() {
        val state = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            draftManager?.saveDraft(
                selectedSkills = state.selectedSkills,
                selectedMajors = state.selectedMajors,
                sessionPrice = state.sessionPrice,
                availability = state.availability
            )
        }
    }

    private fun loadMajors() {
        if (!NetworkMonitor.isOnline.value) {
            viewModelScope.launch(Dispatchers.IO) {
                val cachedMajors = draftManager?.getMajors()
                _uiState.update { it.copy(
                    majors = cachedMajors ?: emptyList(),
                    error = if (cachedMajors == null) "Sin conexión para cargar facultades" else null
                ) }
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = withContext(Dispatchers.IO) {
                repository.getMajors()
            }
            result.onSuccess { majors ->
                withContext(Dispatchers.IO) {
                    draftManager?.saveMajors(majors)
                }
                _uiState.update { it.copy(majors = majors, isLoading = false) }
            }.onFailure { e ->
                val cached = withContext(Dispatchers.IO) {
                    draftManager?.getMajors()
                }
                _uiState.update { it.copy(
                    majors = cached ?: emptyList(),
                    isLoading = false,
                    error = if (cached == null) "Error cargando habilidades/carreras. Revise su conexion." else null
                ) }
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
        if (!NetworkMonitor.isOnline.value) {
            viewModelScope.launch(Dispatchers.IO) {
                val cachedSkills = draftManager?.getSkillsForMajor(major)
                if (cachedSkills != null) {
                    _uiState.update { state ->
                        state.copy(skillsByMajor = state.skillsByMajor + (major to cachedSkills))
                    }
                }
            }
            return
        }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getSkillsByMajor(major)
            }
            result.onSuccess { skills ->
                withContext(Dispatchers.IO) {
                    draftManager?.saveSkillsForMajor(major, skills)
                }
                _uiState.update { state ->
                    state.copy(skillsByMajor = state.skillsByMajor + (major to skills))
                }
            }.onFailure {
                val cached = withContext(Dispatchers.IO) {
                    draftManager?.getSkillsForMajor(major)
                }
                _uiState.update { state ->
                    state.copy(skillsByMajor = state.skillsByMajor + (major to (cached ?: emptyList())))
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
                val apiAvailability = AvailabilityDto(
                    monday = currentState.availability["LUN"]?.map { formatToIso(it.from) } ?: emptyList(),
                    tuesday = currentState.availability["MAR"]?.map { formatToIso(it.from) } ?: emptyList(),
                    wednesday = currentState.availability["MIE"]?.map { formatToIso(it.from) } ?: emptyList(),
                    thursday = currentState.availability["JUE"]?.map { formatToIso(it.from) } ?: emptyList(),
                    friday = currentState.availability["VIE"]?.map { formatToIso(it.from) } ?: emptyList(),
                    saturday = currentState.availability["SAB"]?.map { formatToIso(it.from) } ?: emptyList()
                )

                val resultError = withContext(Dispatchers.IO) {
                    val selectedMajor = currentState.selectedMajors.firstOrNull()
                    if (selectedMajor != null) {
                        val majorResponse = userRepository.updateMajor(userId, selectedMajor)
                        if (!majorResponse.isSuccessful) return@withContext "Error actualizando carrera"
                    }

                    val skillsResponse = userRepository.updateTutoringSkills(userId, currentState.selectedSkills.toList())
                    if (!skillsResponse.isSuccessful) return@withContext "Error actualizando habilidades"

                    val availabilityResponse = userRepository.updateAvailability(userId, apiAvailability)
                    if (!availabilityResponse.isSuccessful) return@withContext "Error actualizando disponibilidad"

                    val priceResponse = userRepository.updateSessionPrice(userId, currentState.sessionPrice)
                    if (!priceResponse.isSuccessful) return@withContext "Error actualizando precio"
                    
                    null
                }

                if (resultError == null) {
                    withContext(Dispatchers.IO) {
                        draftManager?.clearDraft()
                    }
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resultError) }
                }
                
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error inesperado: ${e.message}") }
            }
        }
    }
    
    private fun formatToIso(time: String): String {
        return "2024-11-20T$time:00Z"
    }
}
