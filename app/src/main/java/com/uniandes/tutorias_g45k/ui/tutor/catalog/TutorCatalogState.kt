package com.uniandes.tutorias_g45k.ui.tutor.catalog

import androidx.compose.ui.text.input.TextFieldValue
import com.uniandes.tutorias_g45k.data.catalog.ReviewResponse
import com.uniandes.tutorias_g45k.data.catalog.TutorResponse
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto

data class CatalogoUiState(
    val searchText: String = "",
    val searchTextValue: TextFieldValue =TextFieldValue(""),
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
    val isOnline: Boolean = true,
    val error: String? = null,
    val reviewSuccess: Boolean = false
)
