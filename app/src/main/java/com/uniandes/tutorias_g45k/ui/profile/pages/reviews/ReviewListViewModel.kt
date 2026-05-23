package com.uniandes.tutorias_g45k.ui.profile.pages.reviews

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.profile.ProfileRepoProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewListViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ReviewListUiState())
    private val profileRepository = ProfileRepoProvider.getRepository()
    private val authRepository = AuthHolder.authRepo

    val uiState = _uiState.asStateFlow()

    private val _dayFilter = MutableStateFlow<Int?>(0)
    val dayFilter: StateFlow<Int?> = _dayFilter.asStateFlow()

    private val _isTutor = MutableStateFlow(false)
    val isTutor: StateFlow<Boolean> = _isTutor.asStateFlow()

    // Profiling: flatMapLatest$1.invoke appeared 4x in cpu-clock samples.
    // reLoadReviews() used _dayFilter.value = null then _dayFilter.value = current,
    // causing combine() to emit twice per call → 2 flatMapLatest restarts per reload,
    // each cancelling and re-launching an inner coroutine. A dedicated SharedFlow
    // trigger emits exactly once per manual refresh.
    private val _refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenReviews() {
        Log.d("ReviewListViewModel", "1. Entrando a listenReviews")
        viewModelScope.launch {
            combine(
                _isTutor,
                _dayFilter,
                _refreshTrigger.onStart { emit(Unit) }
            ) { tutor, dayRange, _ ->
                Pair(tutor, dayRange)
            }
            .flatMapLatest { (tutor, dayRange) ->
                Log.d("ReviewListViewModel", "2. Filtro cambiado a: $dayRange")
                _uiState.update { it.copy(isLoading = true, error = "") }
                profileRepository.getReviews(authRepository.getCurrentUser()?.uid ?: "", tutor, dayRange)
            }
            .catch { it ->
                Log.e("ReviewListViewModel", "Error en stream", it)
                _uiState.update { state ->
                    state.copy(
                        error = "Error recuperando reseñas, revise su conexión a internet.",
                        isLoading = false,
                        reviews = emptyList()
                    )
                }
            }
            .collect { list ->
                Log.d("ReviewListViewModel", "4. DATOS RECIBIDOS: ${list.size} elementos")
                _uiState.update { it.copy(reviews = list, isLoading = false, numReviews = list.size) }
            }
        }
    }

    fun onSelectRange(range: Int?) {
        _dayFilter.value = range
    }

    fun onSelectTutor(isTutor:Boolean){
        _isTutor.value=isTutor
        reLoadReviews()
    }

    fun reLoadReviews() {
        _uiState.update { it.copy(error = "") }
        _refreshTrigger.tryEmit(Unit)
    }

    init{
        listenReviews()
    }

}