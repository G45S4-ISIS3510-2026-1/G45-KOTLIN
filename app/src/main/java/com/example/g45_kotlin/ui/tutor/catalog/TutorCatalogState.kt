package com.example.g45_kotlin.ui.tutor.catalog

import com.example.g45_kotlin.data.catalog.ReviewResponse
import com.example.g45_kotlin.data.catalog.TutorResponse
import com.example.g45_kotlin.data.user.TutorSummaryDto

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
