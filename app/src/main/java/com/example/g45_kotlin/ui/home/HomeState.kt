package com.example.g45_kotlin.ui.home

import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.data.recommendation.TutorSummaryDto

data class HomeState(
    val userName: String = "Amigo",
    val nextSessions: List<SessionDto> = emptyList(),
    val sessionError: String? = null,
    val featuredTutors: List<TutorSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val areSessionLoading: Boolean = false,
    val error: String? = null
)
