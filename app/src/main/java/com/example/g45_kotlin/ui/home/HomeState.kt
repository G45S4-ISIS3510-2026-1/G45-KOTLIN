package com.example.g45_kotlin.ui.home

import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.data.user.TutorSummaryDto

data class HomeState(
    val userName: String = "Amigo",
    val nextSession: SessionDto? = null,
    val featuredTutors: List<TutorSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
