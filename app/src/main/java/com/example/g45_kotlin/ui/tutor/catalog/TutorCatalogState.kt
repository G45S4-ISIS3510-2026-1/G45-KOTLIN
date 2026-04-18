package com.example.g45_kotlin.ui.tutor.catalog

import com.example.g45_kotlin.data.catalog.ReviewResponse
import com.example.g45_kotlin.data.catalog.TutorResponse
import com.example.g45_kotlin.data.user.TutorSummaryDto

data class CatalogoUiState(
    val searchText: String = "",
    val tutores: List<TutorSummaryDto> = emptyList(),
    val filtrados: List<TutorSummaryDto> = emptyList(),
    val visibleTutores: List<TutorSummaryDto> = emptyList(),
    val currentPage: Int = 1,
    val selectedOrder: String = "Mejor Rating",
    val selectedFacultad: String = "Todas",
    val onlyFavorites: Boolean = false,
    val selectedTutor: TutorResponse? = null,
    val selectedTutorReviews: List<ReviewResponse> = emptyList(),
    val selectedTutorSkills: List<String> = emptyList(),
    val favoriteTutorIds: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingReviews: Boolean = false,
    val error: String? = null
)
