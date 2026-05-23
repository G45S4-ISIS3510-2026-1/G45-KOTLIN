package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import com.uniandes.tutorias_g45k.data.firestore.FirestorePqrDto

data class PqrListUiState(
    val isLoading: Boolean = false,
    val pqrs: List<FirestorePqrDto> = emptyList(),
    val error: String = "",
    val connected: Boolean = true
)
