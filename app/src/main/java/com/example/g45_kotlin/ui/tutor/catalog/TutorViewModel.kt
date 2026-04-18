package com.example.g45_kotlin.ui.tutor.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.catalog.CreateReviewRequest
import com.example.g45_kotlin.data.catalog.ReviewResponse
import com.example.g45_kotlin.data.catalog.TutorRepository
import com.example.g45_kotlin.data.catalog.TutorResponse
import com.example.g45_kotlin.data.reservation.ReservationRepository
import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.data.user.TutorSummaryDto
import com.example.g45_kotlin.data.user.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorViewModel(
    private val repository: TutorRepository = TutorRepository(),
    private val reservationRepository: ReservationRepository = ReservationRepository,
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()
    private val PAGE_SIZE = 10

    init {
        cargarTutores()
        cargarFavoritos()
    }

    fun cargarTutores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.searchTutors()
            result.onSuccess { tutores ->
                _uiState.update { state ->
                    val filtrados = aplicarFiltros(tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, state.favoriteTutorIds)
                    state.copy(
                        isLoading = false,
                        tutores = tutores,
                        filtrados = filtrados,
                        visibleTutores = filtrados.take(PAGE_SIZE),
                        currentPage = 1
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar tutores: ${error.message}") }
            }
        }
    }

    fun loadNextPage() {
        _uiState.update { state ->
            val nextPage = state.currentPage + 1
            val nextVisibleCount = nextPage * PAGE_SIZE
            if (state.visibleTutores.size < state.filtrados.size) {
                state.copy(
                    visibleTutores = state.filtrados.take(nextVisibleCount),
                    currentPage = nextPage
                )
            } else {
                state
            }
        }
    }

    private fun cargarFavoritos() {
        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return
        viewModelScope.launch {
            try {
                val response = userRepository.getUser(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    val favIds = user?.favTutors ?: user?.favTutorsSnake ?: emptyList()
                    _uiState.update { state ->
                        val filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, favIds)
                        state.copy(
                            favoriteTutorIds = favIds,
                            filtrados = filtrados,
                            visibleTutores = filtrados.take(PAGE_SIZE),
                            currentPage = 1
                        )
                    }
                }
            } catch (e: Exception) {
                println("Error cargando favoritos: ${e.message}")
            }
        }
    }

    fun toggleFavorite(tutorId: String) {
        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return
        val oldFavs = _uiState.value.favoriteTutorIds
        val currentFavs = oldFavs.toMutableList()
        
        if (currentFavs.contains(tutorId)) {
            currentFavs.remove(tutorId)
        } else {
            currentFavs.add(tutorId)
        }

        // Actualización optimista
        _uiState.update { state ->
            val filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, currentFavs)
            state.copy(
                favoriteTutorIds = currentFavs,
                filtrados = filtrados,
                visibleTutores = filtrados.take(PAGE_SIZE),
                currentPage = 1
            )
        }

        viewModelScope.launch {
            try {
                val response = userRepository.updateFavoriteTutors(userId, currentFavs)
                if (!response.isSuccessful) {
                    // Revertir si falla
                    _uiState.update { state ->
                        val filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, oldFavs)
                        state.copy(
                            favoriteTutorIds = oldFavs,
                            filtrados = filtrados,
                            visibleTutores = filtrados.take(PAGE_SIZE),
                            currentPage = 1,
                            error = "Error al sincronizar favoritos"
                        )
                    }
                }
            } catch (e: Exception) {
                // Revertir si falla
                _uiState.update { state ->
                    val filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, oldFavs)
                    state.copy(
                        favoriteTutorIds = oldFavs,
                        filtrados = filtrados,
                        visibleTutores = filtrados.take(PAGE_SIZE),
                        currentPage = 1,
                        error = "Error al sincronizar favoritos: ${e.message}"
                    )
                }
            }
        }
    }

    fun onOnlyFavoritesChange(onlyFavs: Boolean) {
        _uiState.update { state ->
            val filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, onlyFavs, state.favoriteTutorIds)
            state.copy(
                onlyFavorites = onlyFavs,
                filtrados = filtrados,
                visibleTutores = filtrados.take(PAGE_SIZE),
                currentPage = 1
            )
        }
    }

    fun onSearchTextChange(text: String) {
        _uiState.update { state ->
            val filtrados = aplicarFiltros(state.tutores, text, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, state.favoriteTutorIds)
            state.copy(
                searchText = text,
                filtrados = filtrados,
                visibleTutores = filtrados.take(PAGE_SIZE),
                currentPage = 1
            )
        }
    }

    fun onOrderChange(order: String) {
        _uiState.update { state ->
            val filtrados = aplicarFiltros(state.tutores, state.searchText, order, state.selectedFacultad, state.onlyFavorites, state.favoriteTutorIds)
            state.copy(
                selectedOrder = order,
                filtrados = filtrados,
                visibleTutores = filtrados.take(PAGE_SIZE),
                currentPage = 1
            )
        }
    }

    fun onFacultadChange(facultad: String) {
        _uiState.update { state ->
            val filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, facultad, state.onlyFavorites, state.favoriteTutorIds)
            state.copy(
                selectedFacultad = facultad,
                filtrados = filtrados,
                visibleTutores = filtrados.take(PAGE_SIZE),
                currentPage = 1
            )
        }
    }

    fun onTutorSelected(tutor: TutorSummaryDto?) {
        if (tutor != null) {
            _uiState.update { it.copy(selectedTutor = TutorResponse(
                id = tutor.id,
                email = "",
                name = tutor.name,
                profileImageUrl = tutor.profileImageUrl
            ), error = null) }
            
            viewModelScope.launch {
                val result = repository.getTutorDetail(tutor.id ?: "")
                result.onSuccess { detailedTutor ->
                    _uiState.update { it.copy(selectedTutor = detailedTutor) }
                }.onFailure { error ->
                    println("Error cargando detalle extra: ${error.message}")
                }
                actualizarReseñas(tutor.id ?: "")
                val skillsResult = repository.getTutorSkillsByIds(uiState.value.selectedTutor?.tutoringSkills ?: emptyList())
                skillsResult.onSuccess { skills ->
                    _uiState.update { it.copy(selectedTutorSkills = skills) }
                }.onFailure { error ->
                    println("Error cargando habilidades: ${error.message}")
                }
            }
        } else {
            _uiState.update { it.copy(selectedTutor = null) }
        }
    }

    private suspend fun actualizarReseñas(tutorId: String) {
        _uiState.update { it.copy(isLoadingReviews = true) }
        val reviewsResult = repository.getTutorReviews(tutorId)
        reviewsResult.onSuccess { reviews ->
            val fixedReviews: List<ReviewResponse> = reviews.map { it.copy(createdAt = formatearFecha(it.createdAt)) }
            _uiState.update { it.copy(selectedTutorReviews = fixedReviews, isLoadingReviews = false) }
        }.onFailure { error ->
            println("Error cargando reseñas: ${error.message}")
            _uiState.update { it.copy(isLoadingReviews = false) }
        }
    }

    fun createReview(rating: Float, comment: String) {
        val tutorId = _uiState.value.selectedTutor?.id ?: return
        val authorId = AuthHolder.authRepo.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 1. Verificar si el usuario ya hizo una reseña a este tutor
                val authorReviewsDeferred = async { repository.getReviewsByAuthor(authorId) }
                
                // 2. Verificar si el usuario ha tenido sesiones con el tutor
                val sessionsBetweenDeferred = async { reservationRepository.getSessionsBetween(authorId, tutorId) }

                val authorReviewsResult = authorReviewsDeferred.await()
                val sessionsBetweenResponse = sessionsBetweenDeferred.await()

                val authorReviews = authorReviewsResult.getOrNull() ?: emptyList()
                // Verificamos por tutorId o tutorIdSnake por si acaso
                val alreadyReviewed = authorReviews.any { it.tutorId == tutorId || it.tutorIdSnake == tutorId }
                
                val sessions: List<SessionDto> = sessionsBetweenResponse.body() ?: emptyList()
                val hasHadSession = sessions.any { it.status.uppercase() == "COMPLETED" || it.status.uppercase() == "CONFIRMED" }

                if (alreadyReviewed) {
                    _uiState.update { it.copy(isLoading = false, error = "Ya has calificado a este tutor.") }
                    return@launch
                }

                if (!hasHadSession) {
                    _uiState.update { it.copy(isLoading = false, error = "Debes haber tenido al menos una sesión con el tutor para calificarlo.") }
                    return@launch
                }

                val request = CreateReviewRequest(
                    tutorId = tutorId,
                    authorId = authorId,
                    rating = rating,
                    label = "Reseña",
                    details = comment
                )
                val result = repository.createReview(request)
                result.onSuccess {
                    actualizarReseñas(tutorId)
                    _uiState.update { it.copy(isLoading = false) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al crear reseña: ${error.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error inesperado: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun aplicarFiltros(tutores: List<TutorSummaryDto>, text: String, order: String, facultad: String, onlyFavs: Boolean, favIds: List<String>): List<TutorSummaryDto> {
        var lista = tutores
        if (onlyFavs) {
            lista = lista.filter { favIds.contains(it.id) }
        }
        if (facultad != "Todas") {
            lista = lista.filter { mapearFacultad(it.major) == facultad }
        }
        if (text.isNotEmpty()) {
            lista = lista.filter { tutor ->
                tutor.name.contains(text, ignoreCase = true) ||
                tutor.major.contains(text, ignoreCase = true)
            }
        }
        return when (order) {
            "Mejor Rating" -> lista.sortedByDescending { it.rating }
            "Precio" -> lista.sortedBy { it.sessionPrice }
            else -> lista
        }
    }

    private fun mapearFacultad(major: String): String {
        return when {
            major.contains("Ingeniería", ignoreCase = true) -> "Ingeniería"
            major.contains("Matemáticas", ignoreCase = true) || major.contains("Física", ignoreCase = true) -> "Ciencias"
            major.contains("Economía", ignoreCase = true) || major.contains("Administración", ignoreCase = true) -> "Economía"
            major.contains("Artes", ignoreCase = true) || major.contains("Diseño", ignoreCase = true) -> "Artes"
            else -> "Otras"
        }
    }

    private fun formatearFecha(isoDate: String): String {
        return isoDate.split("T").firstOrNull() ?: "Reciente"
    }
}
