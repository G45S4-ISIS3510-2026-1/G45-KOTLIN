package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
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

    private val profileRepository = ProfileRepoProvider.getRepository()
    private val authRepository = AuthHolder.authRepo

    init {
        loadPqrs()
    }

    fun loadPqrs() {
        _uiState.update { it.copy(isLoading = true, error = "") }

        viewModelScope.launch(Dispatchers.IO) {
            val userId = authRepository.getCurrentUser()?.uid ?: ""
            if (userId.isBlank()) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                }
                return@launch
            }

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
}
