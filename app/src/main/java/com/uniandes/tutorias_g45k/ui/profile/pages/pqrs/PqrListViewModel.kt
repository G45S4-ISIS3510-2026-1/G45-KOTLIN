package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.profile.ProfileRepoFirestoreImp
import com.uniandes.tutorias_g45k.data.profile.ProfileRepository
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PqrListViewModel(
    private val profileRepository: ProfileRepository = ProfileRepoFirestoreImp,
    private val reservationRepository: ReservationRepository = ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PqrListUiState())
    val uiState: StateFlow<PqrListUiState> = _uiState.asStateFlow()

    private val _dayFilter = MutableStateFlow<Int?>(null)
    val dayFilter: StateFlow<Int?> = _dayFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    // Profiling: PqrListViewModel.<init> appeared 6x in cpu-clock samples,
    // indicating the VM is reconstructed multiple times per session. Each
    // construction fires fetchPqrs() in init{}. Without cancellation, prior
    // fetches keep running concurrently alongside new ones.
    private var fetchJob: Job? = null

    init {
        fetchPqrs()
    }

    fun fetchPqrs() {
        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return
        val dayRange = _dayFilter.value
        val status = _statusFilter.value
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getPQRS(userId, dayRange)
                .onSuccess { pqrs ->
                    val filtered = if (status != null) pqrs.filter { it.status == status } else pqrs
                    val displayItems = filtered.map { pqr ->
                        async {
                            val incidentId = pqr.relatedIncident
                            val imageUrl = if (!incidentId.isNullOrBlank()) {
                                reservationRepository.getSession(incidentId)
                                    .getOrNull()
                                    ?.let { session ->
                                        if (session.student.id == userId) session.tutor.profileImageUrl
                                        else session.student.profileImageUrl
                                    }
                            } else {
                                pqr.authorId?.takeIf { it.isNotBlank() }?.let { authorId ->
                                    profileRepository.getProfile(authorId).getOrNull()?.profileImageUrl
                                }
                            }
                            PqrDisplayItem(pqr = pqr, imageUrl = imageUrl)
                        }
                    }.awaitAll()
                    _uiState.update { it.copy(displayItems = displayItems, isLoading = false, error = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onSelectDayRange(range: Int?) {
        _dayFilter.value = range
        fetchPqrs()
    }

    fun onSelectStatus(status: String?) {
        _statusFilter.value = status
        fetchPqrs()
    }
}
