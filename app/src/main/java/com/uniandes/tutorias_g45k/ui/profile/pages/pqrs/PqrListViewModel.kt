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
import com.uniandes.tutorias_g45k.data.profile.ProfileRepoProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PqrListViewModel : ViewModel() {
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
    private val profileRepository = ProfileRepoProvider.getRepository()
    private val authRepository = AuthHolder.authRepo

    fun loadPqrs() {
        _uiState.update { it.copy(isLoading = true, error = "") }
        // MULTITHREADING: Operación asíncrona enviada al hilo de I/O para no bloquear el Main Thread
        viewModelScope.launch(Dispatchers.IO) {
            val userId = authRepository.getCurrentUser()?.uid ?: ""
            if (userId.isBlank()) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                }
                return@launch
            }

            // Llamada suspendida simple en lugar de Flow, cumpliendo la filosofía de minimalismo
            val result = profileRepository.getPQRS(userId)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    val pqrsList = result.getOrNull() ?: emptyList()
                    _uiState.update { it.copy(pqrs = pqrsList, isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exceptionOrNull()?.message ?: "Error cargando PQRs"
                        )
                    }
                }
            }
        }
    }

    init {
        loadPqrs()
    }
}