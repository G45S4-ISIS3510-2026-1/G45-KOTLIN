package com.uniandes.tutorias_g45k.ui.novelties

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.novelty.NoveltyRepoProvider
import com.uniandes.tutorias_g45k.data.novelty.NoveltyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoveltyViewModel : ViewModel() {
    private val noveltyRepo: NoveltyRepository = NoveltyRepoProvider.getNoveltyRepo()
    private val currentUserId = AuthHolder.authRepo.getCurrentUser()?.uid ?: ""

    private val _noveltyState = MutableStateFlow(NoveltyScreenState(isLoading = true))
    val noveltyState: StateFlow<NoveltyScreenState> = _noveltyState.asStateFlow()

    private val _dayFilter = MutableStateFlow<Int?>(null)
    val dayFilter: StateFlow<Int?> = _dayFilter.asStateFlow()

    init {
        listenNovelties()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenNovelties() {
        Log.d("DEBUG_NOTI", "1. Entrando a listenNovelties")
        viewModelScope.launch {
            _dayFilter
                .flatMapLatest { dayRange ->
                    Log.d("DEBUG_NOTI", "2. Filtro cambiado a: $dayRange")
                    _noveltyState.update { it.copy(isLoading = true, error = null) }
                    noveltyRepo.getUnreadNovelties(currentUserId, dayRange)
                }
                .catch { it ->
                    Log.e("NoveltyViewModel", "Error en stream", it)
                    _noveltyState.update { state ->
                        state.copy(
                            error = "Error recuperando novedades, por favor revise su conexión a internet y refresque el listado",
                            isLoading = false,
                            novelties = emptyList()
                        )
                    }
                }
                .collect { list ->
                    Log.d("DEBUG_NOTI", "4. DATOS RECIBIDOS: ${list.size} elementos: $list")
                    _noveltyState.update { it.copy(novelties = list, isLoading = false) }
                }
        }
    }

    fun onSelectRange(range: Int?) {
        _dayFilter.value = range
    }

    fun discardNovelty(noveltyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                noveltyRepo.markNoveltyAsRead(noveltyId)
            } catch (e: Exception) {
                Log.e("NoveltyViewModel", "Error al descartar notificación", e)
            }
        }
    }

    fun reLoadNovelties() {
        val current = _dayFilter.value
        _dayFilter.value = current
    }
}
