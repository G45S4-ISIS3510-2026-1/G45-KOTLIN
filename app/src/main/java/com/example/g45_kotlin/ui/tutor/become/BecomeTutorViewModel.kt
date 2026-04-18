package com.example.g45_kotlin.ui.tutor.become

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.catalog.TutorRepository
import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BecomeTutorUiState(
    val majors: List<String> = emptyList(),
    val skillsByMajor: Map<String, List<SkillSummaryDto>> = emptyMap(),
    val expandedMajors: Set<String> = emptySet(),
    val selectedSkills: Set<String> = emptySet(),
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
    private val repository: TutorRepository = TutorRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(BecomeTutorUiState())
    val uiState: StateFlow<BecomeTutorUiState> = _uiState.asStateFlow()

    init {
        loadMajors()
    }

    private fun loadMajors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getMajors().onSuccess { majors ->
                _uiState.update { it.copy(majors = majors, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
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

    private fun loadSkillsForMajor(major: String) {
        viewModelScope.launch {
            repository.getSkillsByMajor(major).onSuccess { skills ->
                _uiState.update { state ->
                    state.copy(skillsByMajor = state.skillsByMajor + (major to skills))
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

    fun updateTimeSlot(day: String, slotId: String, from: String, to: String) {
        val currentSlots = _uiState.value.availability[day] ?: emptyList()
        val newSlots = currentSlots.map {
            if (it.id == slotId) it.copy(from = from, to = to) else it
        }
        _uiState.update { it.copy(availability = it.availability + (day to newSlots)) }
    }

    fun publishProfile() {
        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.becomeTutor(userId).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
