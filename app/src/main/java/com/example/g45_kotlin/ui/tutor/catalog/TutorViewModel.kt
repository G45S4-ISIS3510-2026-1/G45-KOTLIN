package com.example.g45_kotlin.ui.tutor.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.catalog.TutorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorViewModel(
    private val repository: TutorRepository = TutorRepository(),
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
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar tutores: ${error.message}") }
            }
        }
    }

    fun onTutorSelected(tutor: Tutor?) {
        if (tutor != null) {
            // 1. Mostramos el detalle inmediatamente con la info que ya tenemos
            _uiState.update { it.copy(selectedTutor = tutor, error = null) }
            
            // 2. Cargamos detalles extra y reseñas en segundo plano
            viewModelScope.launch {
                val result = repository.getTutorDetail(tutor.id)
                result.onSuccess { detailedTutor ->
                    _uiState.update { it.copy(selectedTutor = detailedTutor) }
                }.onFailure { error ->
                    println("Error cargando detalle extra: ${error.message}")
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

    private fun aplicarFiltros(tutores: List<Tutor>, text: String, order: String, facultad: String): List<Tutor> {
        var lista = tutores
        if (facultad != "Todas") {
            lista = lista.filter { it.facultad == facultad }
        }
        if (text.isNotEmpty()) {
            lista = lista.filter { tutor ->
                tutor.nombre.contains(text, ignoreCase = true) ||
                tutor.carrera.contains(text, ignoreCase = true) ||
                tutor.tags.any { it.contains(text, ignoreCase = true) }
            }
        }
        return when (order) {
            "Mejor Rating" -> lista.sortedByDescending { it.rating }
            "Precio" -> lista.sortedBy { it.precioValor }
            else -> lista
        }
    }
}
