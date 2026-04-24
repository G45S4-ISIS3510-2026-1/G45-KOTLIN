package com.uniandes.tutorias_g45k.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.novelty.NoveltyRepoProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainNoveltyViewModel() : ViewModel() {

    private val currentUserId = AuthHolder.authRepo.getCurrentUser()?.uid ?: ""
    private val noveltyRepo = NoveltyRepoProvider.getNoveltyRepo()




    // StateFlow global (para marcar en el nav banner los mensajes pendientes)
    val unreadCount: StateFlow<Int> = noveltyRepo.getUnreadNovelties(currentUserId, null)
        .map { it.size } // Transformamos la lista al conteo
        .catch { e ->
            Log.e("MainVM", "Error en contador global", e)
            emit(0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )
}