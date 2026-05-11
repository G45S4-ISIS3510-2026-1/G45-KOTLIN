package com.uniandes.tutorias_g45k.ui.tutor.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.auth.AuthRepository
import com.uniandes.tutorias_g45k.data.catalog.CreateReviewRequest
import com.uniandes.tutorias_g45k.data.catalog.ReviewResponse
import com.uniandes.tutorias_g45k.data.catalog.TutorRepository
import com.uniandes.tutorias_g45k.data.catalog.TutorResponse
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import com.uniandes.tutorias_g45k.data.reservation.SessionDto
import com.uniandes.tutorias_g45k.data.user.UserRepository
import com.uniandes.tutorias_g45k.utilities.AnalyticsManager
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TutorViewModel(
    private val repository: TutorRepository = TutorRepository,
    private val reservationRepository: ReservationRepository = ReservationRepository,
    private val userRepository: UserRepository = UserRepository

) : ViewModel() {

    private val authRepo: AuthRepository = AuthHolder.authRepo
    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()
    private val PAGE_SIZE = 10

    init {
        observeNetwork()
        cargarTutores()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            NetworkMonitor.isOnline.collect { isOnline ->
                _uiState.update { it.copy(isOnline = isOnline) }
                if (isOnline && _uiState.value.error != null) {
                    cargarTutores() // Reintentar automáticamente cuando vuelve la conexión
                }
            }
        }
    }

    fun cargarTutores() {
        if (!NetworkMonitor.isOnline.value) {
            _uiState.update { it.copy(isLoading = false, error = "Sin conexión a Internet") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = withContext(Dispatchers.IO) { repository.searchTutors() }
            result.onSuccess { tutores ->
                val state = _uiState.value
                val filtrados = withContext(Dispatchers.Default) {
                    aplicarFiltros(tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, state.favoriteTutorIds)
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        tutores = tutores,
                        filtrados = filtrados,
                        visibleTutores = filtrados.take(PAGE_SIZE),
                        currentPage = 1
                    )
                }
                cargarFavoritos()
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar tutores, revise su conexión y arrastre para reintentar") }
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
        if (!NetworkMonitor.isOnline.value) return

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { userRepository.getUser(userId) }
                if (response.isSuccessful) {
                    val user = response.body()
                    val favIds = (user?.favTutors ?: emptyList())
                        .filter { it.isNotBlank() }
                        .map { it.trim() }
                        .distinct()

                    val state = _uiState.value
                    val filtrados = withContext(Dispatchers.Default) {
                        aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, favIds)
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            favoriteTutorIds = favIds,
                            filtrados = filtrados,
                            visibleTutores = filtrados.take(PAGE_SIZE),
                            currentPage = 1
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TutorViewModel", "Error al cargar favoritos", e)
            }
        }
    }

    fun toggleFavorite(tutorId: String) {
        val userId = AuthHolder.authRepo.getCurrentUserId() ?: return

        if (!NetworkMonitor.isOnline.value) {
            _uiState.update { it.copy(error = "No hay conexión para sincronizar favoritos") }
            return
        }

        val oldFavs = _uiState.value.favoriteTutorIds
        val currentFavs = oldFavs.toMutableList()

        if (currentFavs.contains(tutorId)) {
            currentFavs.remove(tutorId)
        } else {
            currentFavs.add(tutorId)
        }

        viewModelScope.launch {
            val state = _uiState.value
            val filtrados = withContext(Dispatchers.Default) {
                aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, currentFavs)
            }

            _uiState.update { currentState ->
                currentState.copy(
                    favoriteTutorIds = currentFavs,
                    filtrados = filtrados,
                    visibleTutores = filtrados.take(PAGE_SIZE),
                    currentPage = 1
                )
            }

            try {
                val response = withContext(Dispatchers.IO) { userRepository.updateFavoriteTutors(userId, currentFavs) }
                if (!response.isSuccessful) {
                    Log.e("TutorViewModel", "Error al sincronizar favoritos: ${response.errorBody()?.string()}")
                    val rollbackState = _uiState.value
                    val rollbackFiltrados = withContext(Dispatchers.Default) {
                        aplicarFiltros(rollbackState.tutores, rollbackState.searchText, rollbackState.selectedOrder, rollbackState.selectedFacultad, rollbackState.onlyFavorites, oldFavs)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            favoriteTutorIds = oldFavs,
                            filtrados = rollbackFiltrados,
                            visibleTutores = rollbackFiltrados.take(PAGE_SIZE),
                            currentPage = 1,
                            error = "Error al sincronizar favoritos"
                        )
                    }
                }else{
                    withContext(Dispatchers.IO){authRepo.updateLocalUser(authRepo.getLocalUser()?.copy(favTutors = currentFavs) ?: return@withContext)}
                }
            } catch (e: Exception) {
                val rollbackState = _uiState.value
                val rollbackFiltrados = withContext(Dispatchers.Default) {
                    aplicarFiltros(rollbackState.tutores, rollbackState.searchText, rollbackState.selectedOrder, rollbackState.selectedFacultad, rollbackState.onlyFavorites, oldFavs)
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        favoriteTutorIds = oldFavs,
                        filtrados = rollbackFiltrados,
                        visibleTutores = rollbackFiltrados.take(PAGE_SIZE),
                        currentPage = 1,
                        error = "Error al sincronizar favoritos: ${e.message}"
                    )
                }
            }
        }
    }

    fun onOnlyFavoritesChange(onlyFavs: Boolean) {
        viewModelScope.launch {
            val state = _uiState.value
            val filtrados = withContext(Dispatchers.Default) {
                aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, state.selectedFacultad, onlyFavs, state.favoriteTutorIds)
            }
            _uiState.update { currentState ->
                currentState.copy(
                    onlyFavorites = onlyFavs,
                    filtrados = filtrados,
                    visibleTutores = filtrados.take(PAGE_SIZE),
                    currentPage = 1
                )
            }
        }
    }

    fun onSearchTextChange(text: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val filtrados = withContext(Dispatchers.Default) {
                aplicarFiltros(state.tutores, text, state.selectedOrder, state.selectedFacultad, state.onlyFavorites, state.favoriteTutorIds)
            }
            _uiState.update { currentState ->
                currentState.copy(
                    searchText = text,
                    filtrados = filtrados,
                    visibleTutores = filtrados.take(PAGE_SIZE),
                    currentPage = 1
                )
            }
        }
    }

    fun onOrderChange(order: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val filtrados = withContext(Dispatchers.Default) {
                aplicarFiltros(state.tutores, state.searchText, order, state.selectedFacultad, state.onlyFavorites, state.favoriteTutorIds)
            }
            _uiState.update { currentState ->
                currentState.copy(
                    selectedOrder = order,
                    filtrados = filtrados,
                    visibleTutores = filtrados.take(PAGE_SIZE),
                    currentPage = 1
                )
            }
        }
    }

    fun onFacultadChange(facultad: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val filtrados = withContext(Dispatchers.Default) {
                aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, facultad, state.onlyFavorites, state.favoriteTutorIds)
            }
            _uiState.update { currentState ->
                currentState.copy(
                    selectedFacultad = facultad,
                    filtrados = filtrados,
                    visibleTutores = filtrados.take(PAGE_SIZE),
                    currentPage = 1
                )
            }
        }
    }

    fun onTutorSelected(tutor: TutorSummaryDto?) {
        if (tutor != null) {
            _uiState.update { it.copy(
                selectedTutor = TutorResponse(
                    id = tutor.id,
                    email = "",
                    name = tutor.name,
                    profileImageUrl = tutor.profileImageUrl,
                    major = tutor.major,
                    tutorRating = tutor.rating,
                    sessionPrice = tutor.sessionPrice
                ),
                selectedTutorSkills = emptyList(),
                selectedTutorReviews = emptyList(),
                error = null
            ) }

            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) { repository.getTutorDetail(tutor.id) }
                result.onSuccess { detailedTutor ->
                    _uiState.update { it.copy(selectedTutor = detailedTutor) }
                    val skillsResult = withContext(Dispatchers.IO) { repository.getTutorSkillsByIds(detailedTutor.tutoringSkills) }
                    skillsResult.onSuccess { skills ->
                        _uiState.update { state ->
                            state.copy(selectedTutorSkills = skills.map { it.label })
                        }
                    }
                }
                actualizarReseñas(tutor.id)
                _uiState.update { it.copy(isLoading = false) }
            }
        } else {
            _uiState.update { it.copy(selectedTutor = null, isLoading = false) }
        }
    }

    private suspend fun actualizarReseñas(tutorId: String) {
        _uiState.update { it.copy(isLoadingReviews = true) }
        val reviewsResult = withContext(Dispatchers.IO) { repository.getTutorReviews(tutorId) }
        reviewsResult.onSuccess { reviews ->
            val fixedReviews: List<ReviewResponse> = reviews.map { it.copy(createdAt = formatearFecha(it.createdAt)) }
            _uiState.update { it.copy(selectedTutorReviews = fixedReviews, isLoadingReviews = false) }
        }.onFailure {
            _uiState.update { it.copy(isLoadingReviews = false) }
        }
    }

    fun createReview(rating: Float, comment: String) {
        if (!NetworkMonitor.isOnline.value) {
            _uiState.update { it.copy(error = "No hay conexión para enviar la reseña") }
            return
        }

        val tutorId = _uiState.value.selectedTutor?.id ?: return
        val authorId = AuthHolder.authRepo.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, reviewSuccess = false) }
            try {
                val authorReviewsDeferred = async(Dispatchers.IO) { repository.getReviewsByAuthor(authorId) }
                val sessionsBetweenDeferred = async(Dispatchers.IO) { reservationRepository.getSessionsBetween(authorId, tutorId) }

                val authorReviewsResult = authorReviewsDeferred.await()
                val sessionsBetweenResponse = sessionsBetweenDeferred.await()

                val authorReviews = authorReviewsResult.getOrNull() ?: emptyList()
                val alreadyReviewed = authorReviews.any { it.tutorId == tutorId || it.tutorIdSnake == tutorId }

                val sessions: List<SessionDto> = sessionsBetweenResponse.body() ?: emptyList()
                val hasHadSession = sessions.isNotEmpty()

                if (alreadyReviewed) {
                    _uiState.update { it.copy(isLoading = false, error = "Ya has calificado a este tutor.") }
                    return@launch
                }

                if (!hasHadSession) {
                    _uiState.update { it.copy(isLoading = false, error = "Debes haber tenido al menos una sesión con el tutor para calificarlo.") }
                    return@launch
                }

                val request = CreateReviewRequest(tutorId = tutorId, authorId = authorId, rating = rating, label = "Reseña", details = comment)
                val result = withContext(Dispatchers.IO) { repository.createReview(request) }
                result.onSuccess {
                    actualizarReseñas(tutorId)
                    AnalyticsManager.logReviewSubmit(comment.length)
                    _uiState.update { it.copy(isLoading = false, reviewSuccess = true) }
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

    fun clearReviewSuccess() {
        _uiState.update { it.copy(reviewSuccess = false) }
    }

    private fun aplicarFiltros(tutores: List<TutorSummaryDto>, text: String, order: String, facultad: String, onlyFavs: Boolean, favIds: List<String>): List<TutorSummaryDto> {
        var lista = tutores
        if (onlyFavs) {
            lista = lista.filter { tutor -> favIds.any { favId -> favId == tutor.id } }
            AnalyticsManager.logFilterSelection("favoritos", "favorite")
        }
        if (facultad != "Todas") {
            lista = lista.filter { mapearFacultad(it.major) == facultad }
            AnalyticsManager.logFilterSelection(facultad, "faculty")
        }
        if (text.isNotEmpty()) {
            lista = lista.filter { tutor ->
                tutor.name.contains(text, ignoreCase = true) ||
                tutor.major.contains(text, ignoreCase = true)
            }
        }
        return when (order) {
            "Mejor Rating" -> {AnalyticsManager.logOrderSelection("rating");
                lista.sortedByDescending { it.rating };
            }
            "Precio" -> {AnalyticsManager.logOrderSelection("price");
                lista.sortedBy { it.sessionPrice };}
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
