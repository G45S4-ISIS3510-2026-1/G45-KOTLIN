package com.uniandes.tutorias_g45k.ui.profile.pages.pqrs

import com.uniandes.tutorias_g45k.data.firestore.FirestorePqrDto

data class PqrDisplayItem(
    val pqr: FirestorePqrDto,
    val imageUrl: String?
)

data class PqrListUiState(
    val displayItems: List<PqrDisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
