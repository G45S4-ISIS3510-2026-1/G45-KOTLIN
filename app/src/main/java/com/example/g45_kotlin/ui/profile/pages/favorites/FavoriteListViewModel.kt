package com.example.g45_kotlin.ui.profile.pages.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.profile.ProfileRepoProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteListViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteListUiState())
    private val repo = ProfileRepoProvider.getRepository()
    private val authRepository = AuthHolder.authRepo

    val uiState = _uiState.asStateFlow()




    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenFavorites() {
        Log.d("DEBUG_NOTI", "1. Entrando a listenReservations")
        viewModelScope.launch {
                repo.getFavTutors(authRepository.getCurrentUser()?.uid ?: "")
                .catch{
                    it ->
                    Log.e("FavoriteListViewModel", "Error en stream", it)
                    _uiState.update { state ->
                        state.copy(
                            error = "Error recuperando favoritos, por favor revise su conexión a internet y refresque el listado",
                            isLoading = false,
                            favorites = emptyList()
                        )
                    }

                }
                .collect { list ->
                    Log.d("DEBUG_NOTI", "4. DATOS RECIBIDOS: ${list.size} elementos: $list")
                    _uiState.update { it.copy(favorites = list, isLoading = false) }
                }
        }
    }


    init{
        listenFavorites()
    }

}