package com.uniandes.tutorias_g45k.ui.tutor.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.catalog.ReviewResponse
import com.uniandes.tutorias_g45k.data.catalog.TutorRepository
import com.uniandes.tutorias_g45k.data.catalog.TutorResponse
import com.uniandes.tutorias_g45k.data.user.TutorSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorViewModel(
    private val repository: TutorRepository = TutorRepository(tutorDao = AuthHolder.authRepo.getTutorDao()),
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init {
        cargarTutores()
    }

    fun cargarTutores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.searchTutors()
            result.onSuccess { tutores ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        tutores = tutores,
                        filtrados = aplicarFiltros(tutores, state.searchText, state.selectedOrder, state.selectedFacultad)
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar tutores, revise su conexión y arrastre para reintentar") }
            }
        }
    }

    fun onTutorSelected(tutor: TutorSummaryDto?) {
        if (tutor != null) {
            // 1. Mostramos el detalle inmediatamente con la info que ya tenemos
            _uiState.update { it.copy(selectedTutor = TutorResponse(
                id = tutor.id,
                email = "",
                name = tutor.name,
                profileImageUrl = tutor.profileImageUrl
            ), error = null) }
            
            // 2. Cargamos detalles extra y reseñas en segundo plano
            viewModelScope.launch {
                val result = repository.getTutorDetail(tutor.id?: "")
                result.onSuccess { detailedTutor ->
                    _uiState.update { it.copy(selectedTutor = detailedTutor) }
                }.onFailure { error ->
                    println("Error cargando detalle extra: ${error.message}")
                }
                val reviewsResult = repository.getTutorReviews(tutor.id?: "")
                reviewsResult.onSuccess { reviews ->
                    val fixedReviews:List<ReviewResponse> = reviews.map{it.copy(createdAt = formatearFecha(it.createdAt))}
                    _uiState.update { it.copy(selectedTutorReviews = fixedReviews) }
                }.onFailure { error ->
                    println("Error cargando reseñas: ${error.message}")
                }
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

    fun onSearchTextChange(newText: String) {
        _uiState.update { state ->
            state.copy(
                searchText = newText,
                filtrados = aplicarFiltros(state.tutores, newText, state.selectedOrder, state.selectedFacultad)
            )
        }
    }

    fun onOrderChange(newOrder: String) {
        _uiState.update { state ->
            state.copy(
                selectedOrder = newOrder,
                filtrados = aplicarFiltros(state.tutores, state.searchText, newOrder, state.selectedFacultad)
            )
        }
    }

    fun onFacultadChange(newFacultad: String) {
        _uiState.update { state ->
            state.copy(
                selectedFacultad = newFacultad,
                filtrados = aplicarFiltros(state.tutores, state.searchText, state.selectedOrder, newFacultad)
            )
        }
    }

    private fun aplicarFiltros(tutores: List<TutorSummaryDto>, text: String, order: String, facultad: String): List<TutorSummaryDto> {
        var lista = tutores
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

