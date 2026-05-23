package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.local.PqrDraftManager
import com.uniandes.tutorias_g45k.data.profile.CreatePqrRequest
import com.uniandes.tutorias_g45k.data.profile.ProfileRepoFirestoreImp
import com.uniandes.tutorias_g45k.data.profile.ProfileRepository
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PqrViewModel(
    private val reservationRepository: ReservationRepository = ReservationRepository,
    private val profileRepository: ProfileRepository = ProfileRepoFirestoreImp,
    private val draftManager: PqrDraftManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PqrState())
    val uiState: StateFlow<PqrState> = _uiState.asStateFlow()

    init {
        loadDraft()
        fetchSessions()
    }

    private fun loadDraft() {
        draftManager?.getDraft()?.let { draft ->
            _uiState.update { it.copy(
                type = draft.type,
                selectedReason = draft.selectedReason,
                description = draft.description,
                selectedSessionId = draft.selectedSessionId
            ) }
        }
    }

    private fun saveDraft() {
        val state = _uiState.value
        draftManager?.saveDraft(
            type = state.type,
            selectedReason = state.selectedReason,
            description = state.description,
            selectedSessionId = state.selectedSessionId
        )
    }

    private fun fetchSessions() {
        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return
        viewModelScope.launch {
            reservationRepository.getUserSessions(userId).collect { sessions ->
                _uiState.update { it.copy(sessions = sessions.filter{it.status!="Pendiente"}) }
            }
        }
    }

    fun onTypeSelected(type: String) {
        _uiState.update { it.copy(type = type) }
        saveDraft()
    }

    fun onReasonSelected(reason: String) {
        _uiState.update { it.copy(selectedReason = reason) }
        saveDraft()
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
        saveDraft()
    }

    fun onSessionSelected(sessionId: String) {
        _uiState.update { it.copy(selectedSessionId = if (it.selectedSessionId == sessionId) null else sessionId) }
        saveDraft()
    }

    fun submitReport() {
        val currentState = _uiState.value
        if (currentState.selectedReason.isBlank() || currentState.description.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos obligatorios") }
            return
        }

        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return

        val request = CreatePqrRequest(
            type = currentState.type,
            topic = currentState.selectedReason,
            description = currentState.description,
            authorId = userId,
            relatedIncident = currentState.selectedSessionId,
            status = "Pendiente"
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            profileRepository.createPqr(request)
                .onSuccess {
                    draftManager?.clearDraft()
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al enviar el reporte: ${error.message}") }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
