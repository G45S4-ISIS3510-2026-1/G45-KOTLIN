package com.uniandes.tutorias_g45k.ui.tutor.catalog

import com.uniandes.tutorias_g45k.data.catalog.ReviewResponse
import com.uniandes.tutorias_g45k.data.catalog.TutorResponse
import com.uniandes.tutorias_g45k.data.user.TutorSummaryDto

data class CatalogoUiState(
    val searchText: String = "",
    val tutores: List<TutorSummaryDto> = emptyList(),
    val filtrados: List<TutorSummaryDto> = emptyList(),
    val selectedOrder: String = "Mejor Rating",
    val selectedFacultad: String = "Todas",
    val selectedTutor: TutorResponse? = null,
    val selectedTutorReviews: List<ReviewResponse> = emptyList(),
    val selectedTutorSkills: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

